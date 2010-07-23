package knife.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.common.collect.HashMultimap;

import knife.maven.generated_slim_pom_4_0_0.Model;

/**
 * Internal class used by {@link TopPOMLoader}. Does the
 * actual bootstrapping of the recursion. Also used by 
 * {@POMModel} objects to construct its sub modules when
 * recursing.
 *
 */
class POMLoader {

    protected final InputStream inputStream;
    protected final File pomFile;
    private final HashMultimap<POMModel, POMName> depMap;

    POMLoader(File pom, HashMultimap<POMModel,POMName> p) throws IOException {
        pomFile = pom;
        this.depMap = p;
        inputStream = new FileInputStream(pom);
    }
    
    POMModel getPOMTree() throws JAXBException, IOException {
        Model pom = unmarshal();
        POMModel pomModel = new POMModel(pom, pomFile.getParentFile(), depMap);
        return pomModel;
    }
    
    private Model unmarshal() throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(Model.class.getPackage().getName());
        Unmarshaller u = ctx.createUnmarshaller();
        @SuppressWarnings("unchecked")
        JAXBElement<Model> doc = (JAXBElement<Model>) u.unmarshal(inputStream);
        return doc.getValue();
    }

}
