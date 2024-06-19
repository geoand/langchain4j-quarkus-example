package dev.example;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.net.URL;
import java.util.function.Supplier;

public class Beans {

    @ApplicationScoped
    EmbeddingStore<TextSegment> inMemoryChatMemoryStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @ApplicationScoped
    RetrievalAugmentor retrievalAugmentorSupplier(EmbeddingStore<TextSegment> store, EmbeddingModel model) {
        var retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .maxResults(1)
                .minScore(0.6)
                .build();

        return DefaultRetrievalAugmentor.builder().contentRetriever(retriever).build();
    }

    public void ingest(@Observes StartupEvent event,
                       EmbeddingStore<TextSegment> store,
                       EmbeddingModel embeddingModel) {
        String documentName = "miles-of-smiles-terms-of-use.txt";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(documentName);
        if (resource == null) {
            throw new IllegalStateException("Unable to locate document: '" + documentName + "' on the classpath");
        }

        Log.infof("Ingesting documents...");
        Document document = UrlDocumentLoader.load(resource, new TextDocumentParser());
        var ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(500, 0))
                .build();
        ingestor.ingest(document);
        Log.infof("Ingested document");
    }
}
