package evaluation;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.EmbeddingLayer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class CodeEmbeddingGenerator {

    private final Word2Vec model;

   public CodeEmbeddingGenerator(String modelPath)  {
       this.model = WordVectorSerializer.readWord2VecModel(modelPath);
   }

    public float createEmbedding(ClassOrInterfaceDeclaration declaration) {
       var tokenRange = declaration.getTokenRange();
       if(tokenRange.isEmpty()) {
           return 0f;
       }
       var sourceCodeTokens = StreamSupport.stream(tokenRange.get().spliterator(), false)
               .filter(t -> !t.getCategory().isWhitespaceOrComment())
               .map(JavaToken::asString)
               .map(model::getWordVectorMatrix)
               .filter(Objects::nonNull)
               .toList();
       return reduceEmbeddingDimensionality(sourceCodeTokens);
    }

    private float reduceEmbeddingDimensionality(List<INDArray> embeddings) {
        if(embeddings.isEmpty()) {
            return 0f;
        }
        var t = embeddings.toArray(new INDArray[0]);
        var matrix = Nd4j.vstack(t);
        var meanEmbedding = matrix.mean(0);
        return meanEmbedding.getFloat(0);
    }
}
