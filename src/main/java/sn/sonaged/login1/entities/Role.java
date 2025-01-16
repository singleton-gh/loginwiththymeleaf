package sn.sonaged.login1.entities;



public enum Role
    {
    
        RESPONSABLE_REGIONAL("responsable_regional"),
        RESPONSABLE_DEPARTEMENTAL("responsable_departemental"),
        RESPONSABLE_COMMUNAL("responsable_communal"),
        ADMIN("admin"),
        SUPERADMIN("super_admin"),;
    
        private String value;


        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }