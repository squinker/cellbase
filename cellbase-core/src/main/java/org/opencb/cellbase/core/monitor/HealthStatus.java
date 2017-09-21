package org.opencb.cellbase.core.monitor;

import java.util.Map;

/**
 * Created by fjlopez on 20/09/17.
 */
public class HealthStatus {

    private ApplicationDetails application;
    private DependenciesStatus dependencies;

    public HealthStatus() {
    }

    public DependenciesStatus getDependencies() {
        return dependencies;
    }

    public void setDependencies(DependenciesStatus dependencies) {
        this.dependencies = dependencies;
    }

    public ApplicationDetails getApplication() {
        return application;
    }

    public void setApplication(ApplicationDetails application) {
        this.application = application;
    }

    public static class ApplicationDetails {

        private String maintainer;
        private String server;
        private String started;
        private String uptime;
        private Version version;

        public ApplicationDetails() {
        }

        public String getMaintainer() {
            return maintainer;
        }

        public void setMaintainer(String maintainer) {
            this.maintainer = maintainer;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getStarted() {
            return started;
        }

        public void setStarted(String started) {
            this.started = started;
        }

        public String getUptime() {
            return uptime;
        }

        public void setUptime(String uptime) {
            this.uptime = uptime;
        }

        public Version getVersion() {
            return version;
        }

        public void setVersion(Version version) {
            this.version = version;
        }

        public static class Version {
            private String tagName;
            private String commit;

            public Version(String tagName, String commit) {
                this.tagName = tagName;
                this.commit = commit;
            }

            public String getTagName() {
                return tagName;
            }

            public void setTagName(String tagName) {
                this.tagName = tagName;
            }

            public String getCommit() {
                return commit;
            }

            public void setCommit(String commit) {
                this.commit = commit;
            }
        }
    }

    private class DependenciesStatus {
        private Map<String, DatastoreStatus> mongodb;

        public DependenciesStatus() {
        }

        public Map<String, DatastoreStatus> getMongodb() {
            return mongodb;
        }

        public void setMongodb(Map<String, DatastoreStatus> mongodb) {
            this.mongodb = mongodb;
        }

        private class DatastoreStatus {
            private String responseTime;
            private String role;
            private String repset;

            public DatastoreStatus() {
            }

            public String getResponseTime() {
                return responseTime;
            }

            public void setResponseTime(String responseTime) {
                this.responseTime = responseTime;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public String getRepset() {
                return repset;
            }

            public void setRepset(String repset) {
                this.repset = repset;
            }
        }
    }
}
