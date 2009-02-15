/*
 * Copyright (c) 2008 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Konstantin Krivopustov
 * Created: 17.12.2008 11:53:01
 *
 * $Id$
 */
package com.haulmont.cuba.security;

import com.haulmont.cuba.core.*;
import com.haulmont.cuba.security.entity.*;

import java.util.UUID;

public class RelationsTest extends CubaTestCase
{
    public void testRole() {
        UUID roleId = createRole();

        Transaction tx = Locator.createTransaction();
        try {
            EntityManager em = PersistenceProvider.getEntityManager();

            Role role = em.find(Role.class, roleId);
            em.remove(role);

            tx.commit();
        } finally {
            tx.end();
        }
    }

    public UUID createRole() {
        Transaction tx = Locator.createTransaction();
        try {
            EntityManager em = PersistenceProvider.getEntityManager();

            User user = em.find(User.class, UUID.fromString("60885987-1b61-4247-94c7-dff348347f93"));

            Role role = new Role();
            role.setName("RelationTest");
            em.persist(role);

            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            em.persist(userRole);

            tx.commit();

            return role.getId();
        } finally {
            tx.end();
        }
    }
}
