/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.gui.app.core.sendingmessage.browse.resendmessage;

import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.entity.SendingAttachment;
import com.haulmont.cuba.core.entity.SendingMessage;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@UiController("ResendMessage")
@UiDescriptor("resend-message.xml")
public class ResendMessage extends Screen {
    protected SendingMessage message;

    @Inject
    protected EmailService emailService;
    @Inject
    protected FileLoader fileLoader;
    @Inject
    protected Notifications notifications;
    @Inject
    protected TextField<String> emailTextField;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        if(message!=null) {
            emailTextField.setValue(message.getAddress());
        }
    }

    public void setMessage(SendingMessage message) {
        this.message = message;
    }

    @Subscribe("resendEmailBtn")
    protected void onResendEmailBtnClick(Button.ClickEvent event) {
        if (message != null) {
            EmailInfo emailInfo = new EmailInfo(emailTextField.getValue(), message.getCaption(), emailBody(message));
            emailInfo.setFrom(message.getFrom());
            emailInfo.setBodyContentType(message.getBodyContentType());
            emailInfo.setAttachments(getAttachmentsArray(message.getAttachments()));
            emailInfo.setBcc(message.getBcc());
            emailInfo.setCc(message.getCc());
            emailInfo.setHeaders(parseHeadersString(message.getHeaders()));
            try {
                emailService.sendEmail(emailInfo);
            } catch (EmailException e) {
                throw new RuntimeException("Something went wrong during email resending", e);
            }
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption("Sent!")
                    .withDescription("Email was successfuly resent")
                    .show();
            this.closeWithDefaultAction();
        }
    }

    protected String emailBody(SendingMessage message) {
        if (message.getContentTextFile() != null) {
            try (InputStream inputStream = fileLoader.openStream(message.getContentTextFile());){
                return IOUtils.toString(inputStream, Charset.defaultCharset());
            } catch (FileStorageException | IOException e) {
                throw new RuntimeException("Can't read message body from the file", e);
            }
        }
        return message.getContentText();
    }

    protected List<EmailHeader> parseHeadersString(String headersString) {
        List<EmailHeader> emailHeadersList = new ArrayList<>();
        if (headersString != null) {
            for (String header : headersString.split("\n")) {
                emailHeadersList.add(EmailHeader.parse(header));
            }
        }
        return emailHeadersList;
    }

    protected EmailAttachment[] getAttachmentsArray(List<SendingAttachment> sendingAttachments) {
        EmailAttachment[] emailAttachments = new EmailAttachment[sendingAttachments.size()];
        for (int i = 0; i < sendingAttachments.size(); i++) {
            SendingAttachment sendingAttachment = sendingAttachments.get(i);
            EmailAttachment emailAttachment = new EmailAttachment(
                    sendingAttachment.getContent(),
                    sendingAttachment.getName(),
                    sendingAttachment.getContentId(),
                    sendingAttachment.getDisposition(),
                    sendingAttachment.getEncoding()
            );
            emailAttachments[i] = emailAttachment;
        }
        return emailAttachments;
    }
}
