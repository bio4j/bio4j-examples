/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.bio4j.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class ApplyLayoutAlgoAndFiltersToGexf {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        if (args.length != 3) {
            System.out.println("This program expects three parameters:\n"
                    + "1. Input gexf file \n"
                    + "2. Output gexf file \n"
                    + "3. Algorithm time (minutes)");
        } else {


            int algorithmTimeInMinutes = Integer.parseInt(args[2]);

            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.newProject();
            Workspace workspace = pc.getCurrentWorkspace();
            ImportController importController = Lookup.getDefault().lookup(ImportController.class);
            Container container = importController.importFile(new File(args[0]));

            System.out.println("Importing gexf....");

            container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
            container.setAllowAutoNode(false);

            //Append container to graph structure
            importController.process(container, new DefaultProcessor(), workspace);

            GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
            
            System.out.println("Done!");

            //Layout for 1 minute
            AutoLayout autoLayout = new AutoLayout(algorithmTimeInMinutes, TimeUnit.MINUTES);
            autoLayout.setGraphModel(graphModel);

            ForceAtlas2 layout = new ForceAtlas2(new ForceAtlas2Builder());
            layout.setEdgeWeightInfluence(new Double(0.0));
            layout.setLinLogMode(true);
            layout.setGraphModel(graphModel);
            layout.setBarnesHutOptimize(true);
            layout.setJitterTolerance(new Double(1.0));

            autoLayout.addLayout(layout, 1);
            
            System.out.println("Executing layout algorithm....");
            
            autoLayout.execute();

            System.out.println("Exporting result...");
            
            //Export full graph
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            ec.exportFile(new File(args[1]));


            //--------------------------------------------------------------------------------
            //--------------------------------------------------------------------------------
        }
    }
}
