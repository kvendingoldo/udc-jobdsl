package jobdsl.deploy.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcDestroy {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam {
                    name('VERSION')
                    defaultValue('')
                    description('Will be used latest version if parameter is empty')
                    trim(true)
                }
                stringParam {
                    name('RELEASE_NAME')
                    defaultValue('stage')
                    description('')
                    trim(true)
                }
                stringParam {
                    name('KUBERNETES_BRANCH')
                    defaultValue('master')
                    description('')
                    trim(true)
                }
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        github(jobConfig.job.repository, 'ssh')
                    }
                    branch('refs/heads/${KUBERNETES_BRANCH}')
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
                    caCertificate('')
                }
                preBuildCleanup()
                colorizeOutput()
            }
            steps {
                systemGroovy {
                    source {
                        stringSystemScriptSource {
                            script {
                                script(dslFactory.readFileFromWorkspace('jobdsl/common/groovy/printJobVariablesTable.groovy'))
                                sandbox(false)
                            }
                        }
                    }
                }
                systemGroovy {
                    source {
                        stringSystemScriptSource {
                            script {
                                script(dslFactory.readFileFromWorkspace('jobdsl/common/groovy/validateParamertes.groovy'))
                                sandbox(false)
                            }
                        }

                    }
                }
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
            }
        }
    }
}
