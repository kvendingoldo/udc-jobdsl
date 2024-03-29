package jobdsl.utils.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcCreateReleaseBranch {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam {
                    name('COMMIT')
                    defaultValue(jobConfig.job.branch)
                    description('')
                    trim(true)
                }
                stringParam {
                    name('RELEASE_FAMILY')
                    defaultValue(jobConfig.job.branch)
                    description('')
                    trim(true)
                }
            }
            wrappers {
                colorizeOutput()
                timestamps()
                preBuildCleanup()
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        github(jobConfig.job.repository, 'ssh')
                    }
                    branch('refs/heads/master')
                    extensions {
                        wipeOutWorkspace()
                        submoduleOptions {
                            recursive()
                        }
                    }
                }
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
                envInjectBuilder {
                    propertiesFilePath('variables.txt')
                    propertiesContent('')
                }
            }
            publishers {
                gitPublisher {
                    branchesToPush {
                        branchToPush {
                            branchName('${RELEASE_BRANCH_NAME}')
                            targetRepoName('origin')
                        }
                    }
                    pushOnlyIfSuccess(true)
                    pushMerge(false)
                    forcePush(false)
                }
            }
        }
    }
}
