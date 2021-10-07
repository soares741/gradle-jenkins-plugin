package com.terrafolio.gradle.plugins.jenkins.tasks

import com.terrafolio.gradle.plugins.jenkins.dsl.JenkinsConfigurable
import com.terrafolio.gradle.plugins.jenkins.service.BuildDirService
import org.gradle.api.tasks.Internal

class DumpJenkinsItemsTask extends AbstractDumpJenkinsItemsTask {
    @Internal
    def prettyPrint = true
    @Internal
    def prettyPrintPreserveWhitespace = false;

    public DumpJenkinsItemsTask() {
        super();
        needsCredentials = false
        description = "Dumps item configurations from the local model to files."
    }

    @Override
    public void writeXmlConfigurations(JenkinsConfigurable item, BuildDirService buildDirService, String itemType) {
        eachServer(item) { server, service ->
            def file = new File(buildDirService.makeAndGetDir("${server.name}/${itemType}"), "${item.name}.xml")
            if (prettyPrint) {
                file.withWriter { fileWriter ->
                    def node = new XmlParser().parseText(item.getServerSpecificXml(server));
                    def nodePrinter = new XmlNodePrinter(new PrintWriter(fileWriter))
                    nodePrinter.preserveWhitespace = prettyPrintPreserveWhitespace;
                    nodePrinter.print(node)
                }
            } else {
                def xml = item.getServerSpecificXml(server)
                file.write(xml)
            }
        }
    }
}
