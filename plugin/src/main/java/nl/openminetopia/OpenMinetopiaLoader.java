package nl.openminetopia;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class OpenMinetopiaLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:6.0.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.xerial:sqlite-jdbc:3.46.1.3"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("mysql:mysql-connector-java:8.0.33"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.mariadb.jdbc:mariadb-java-client:3.4.1"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("io.vertx:vertx-core:4.5.11"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("io.vertx:vertx-web:4.5.11"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("io.vertx:vertx-web-client:4.5.11"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("net.objecthunter:exp4j:0.4.8"), null));

        resolver.addRepository(new RemoteRepository.Builder("maven", "default", "https://maven-central-eu.storage-download.googleapis.com/maven2/").build());
        classpathBuilder.addLibrary(resolver);
    }

}
