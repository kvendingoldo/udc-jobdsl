package jobdsl.deploy.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcDeployApplication {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam('BACKEND_VERSION', '', 'Will be used latest version if parameter is empty')
                stringParam('KUBERNETES_REFSPEC', 'refs/heads/master', '')
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        github(jobConfig.job.repository, 'ssh')
                    }
                    branch('${KUBERNETES_REFSPEC}')
                    extensions {
                        wipeOutWorkspace()
                        submoduleOptions {
                            recursive()
                        }
                    }
                }
            }
            wrappers {
                kubectlBuildWrapper {
                  serverUrl(jobConfig.job.kubernetes.endpoint)
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
