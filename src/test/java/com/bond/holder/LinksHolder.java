package com.bond.holder;

public abstract class LinksHolder {
    protected static final String REMOVE_ALL_USERS_FILE_PATH =
            "classpath:database/remove-all-users-from-database.sql";
    protected static final String INSERT_USER_TO_DATABASE_FILE_PATH =
            "classpath:database/insert-user-to-database.sql";
    protected static final String INSERT_FIVE_USERS_FILE_PATH =
            "classpath:database/insert-five-users-to-database.sql";
    protected static final String REMOVE_ALL_USER_ROLES_FILE_PATH =
            "classpath:database/delete-all-users_roles-from-database.sql";
    protected static final String INSERT_COMPANY_OWNER_RELATION_TO_USER_ROLES_FILE_PATH =
            "classpath:database/insert-company_owner-to-user_roles.sql";
    protected static final String INSERT_ADMIN_RELATION_TO_USER_ROLES_FILE_PATH =
            "classpath:database/insert-admin-to-user_roles.sql";
}
