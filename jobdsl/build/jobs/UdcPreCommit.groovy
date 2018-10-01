package jobdsl.build.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcPreCommit {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            concurrentBuild()
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            environmentVariables {
                env('GENERATED_VERSION_TYPE', jobConfig.job.generatedVersionType)
                overrideBuildParameters(true)
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
                        refspec('+refs/pull/*:refs/remotes/origin/pr/*')
                    }
                    branch('${sha1}')
                    extensions {
                        wipeOutWorkspace()
                        submoduleOptions {
                            recursive()
                        }
                    }
                }
            }
            triggers {
                githubPullRequest {
                    cron('*/1 * * * *')
                    permitAll()
                }
            }
            steps {
              gitHubSetCommitStatusBuilder {
                statusMessage {
                    content('pending')
                }
              }
              shell('gcloud docker -a')
              shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
              envInjectBuilder {
                  propertiesFilePath('version.properties')
                  propertiesContent('')
              }
              buildNameUpdater {
                  fromFile(false)
                  buildName('${VERSION}')
                  fromMacro(false)
                  macroTemplate('')
                  macroFirst(false)
              }
              maven {
                goals('clean install')
                goals('-B')
                goals('-C')
                goals('-q')
                goals(' -Pimage')
                goals('-Ddocker.registry.host=gcr.io')
              }
            }
            publishers {
              gitHubCommitNotifier {
                  resultOnFailure('fail')
                  statusMessage {
                    content('ok')
                  }
              }
            }
        }
    }
}
