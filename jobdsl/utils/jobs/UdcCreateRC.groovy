package jobdsl.utils.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcCreateRC {
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
                    name('BRANCH')
                    defaultValue(jobConfig.job.branch)
                    description('')
                    trim(true)
                }
                stringParam {
                    name('VERSION')
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
                    branch('refs/remotes/${BRANCH}')
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
                shell('git checkout -f "${COMMIT}"')
                maven {
                    goals('versions:set -DnewVersion="${VERSION}"')
                }
                gitHubSetCommitStatusBuilder {
                    statusMessage {
                        content('rc is building...')
                    }
                }
                buildNameUpdater {
                    fromFile(false)
                    buildName('${VERSION}')
                    fromMacro(true)
                    macroTemplate('${VERSION}')
                    macroFirst(false)
                }
                shell('gcloud docker -a')
                maven {
                    goals('clean deploy')
                    goals('-B')
                    goals('-C')
                    goals('-q')
                    goals(' -Pimage')
                    goals('-Ddocker.registry.host=gcr.io')
                    goals('-Ddocker.repository=university-course/udc/rc/${RELEASE_FAMILY}')
                    injectBuildVariables(false)
                }
            }
            publishers {
                gitHubCommitNotifier {
                    resultOnFailure('fail')
                    statusMessage {
                        content('rc is ready')
                    }
                }
            }
        }
    }
}
