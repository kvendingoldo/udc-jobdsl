package jobdsl.deploy.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcDeployApplication {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            parameters {
                stringParam('BACKEND_VERSION', '', 'Will be used latest version if parameter is empty')
                stringParam('WEB_CONSOLE_VERSION', 'latest', 'Will be used latest version if parameter is empty')
                stringParam('KUBERNETES_REFSPEC', 'refs/heads/master', '')
                stringParam('CONFIG_REFSPEC', 'refs/heads/configs-master', '')
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        github(jobConfig.job.repository)
                    }
                    branch('${KUBERNETES_REFSPEC}')
                }
            }
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            wrappers {
                kubectlBuildWrapper {
                  serverUrl(jobConfig.job.gcp.endpoints.kubernetes)
                  credentialsId(jobConfig.job.credentials.kubernetes)
                  caCertificate("")
                }
                preBuildCleanup()
                colorizeOutput()
            }
            steps {
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.destroyScript))
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.deployScript))
            }
        }
    }
}
