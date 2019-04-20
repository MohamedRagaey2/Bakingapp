package m.ragaey.mohamed.bakingapp.Network;

import java.util.List;

import m.ragaey.mohamed.bakingapp.Models.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();
}
