package nl.openminetopia.modules.data.utils;

import com.craftmend.storm.api.StormModel;
import com.craftmend.storm.api.builders.QueryBuilder;
import lombok.experimental.UtilityClass;
import nl.openminetopia.modules.data.storm.StormDatabase;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@UtilityClass
public class StormUtils {

    /**
     * This method is used to delete models from the database based on the provided filter.
     * It will return a CompletableFuture that will be completed when the deletion is done.
     *
     * @param modelClass    The class of the model to delete
     * @param filterBuilder A consumer that applies filters to identify which models to delete
     *                      (e.g. query -> query.where("some_column", Where.EQUAL, someValue))
     */
    public <M extends StormModel> CompletableFuture<Void> deleteModelData(
            Class<M> modelClass,
            Consumer<QueryBuilder<M>> filterBuilder) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            try {
                QueryBuilder<M> query = StormDatabase.getInstance().getStorm().buildQuery(modelClass);

                // Apply filters to select the models to delete
                filterBuilder.accept(query);

                // Fetch the models that match the query
                Collection<M> models = query.execute().join();

                // Delete each model
                for (M model : models) {
                    StormDatabase.getInstance().getStorm().delete(model);  // Delete the model from the database
                }

                completableFuture.complete(null);  // Mark the future as complete
            } catch (Exception exception) {
                exception.printStackTrace();
                completableFuture.completeExceptionally(exception);  // Handle errors
            }
        });

        return completableFuture;
    }
}
