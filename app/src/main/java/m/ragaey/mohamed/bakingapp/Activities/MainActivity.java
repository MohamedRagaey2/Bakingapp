package m.ragaey.mohamed.bakingapp.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


import m.ragaey.mohamed.bakingapp.Adapters.RecipesAdapter;
import m.ragaey.mohamed.bakingapp.Database.DataBaseBaking;
import m.ragaey.mohamed.bakingapp.Models.Recipe;
import m.ragaey.mohamed.bakingapp.Network.ApiClient;
import m.ragaey.mohamed.bakingapp.Network.ApiService;
import m.ragaey.mohamed.bakingapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.OnRecipeClickListener {

    private ProgressBar progressBar = null;
    private RecipesAdapter recipesAdapter = null;

    private List<Recipe> recipeList = null;
    private List<String> recipeNames = null;

    private DataBaseBaking dataBaseBaking = null;

    private CountingIdlingResource countingIdlingResource = new CountingIdlingResource("IdlingResource");

    private String URL_________ =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBaseBaking = new DataBaseBaking(this);

        initializeViews();

        countingIdlingResource.increment();

        getData();
    }

    private void initializeViews() {

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        int columns;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columns = 1;
        } else {
            columns = 2;
        }

        RecyclerView recipesRecyclerView = findViewById(R.id.recipes_list);
        recipesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(columns,
                StaggeredGridLayoutManager.VERTICAL));
        recipesAdapter = new RecipesAdapter(this, this);

        recipesRecyclerView.setAdapter(recipesAdapter);

    }

    private void getData() {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        if (isConnected()) {
            progressBar.setVisibility(View.VISIBLE);

            Call<List<Recipe>> listCall = apiService.getRecipes();
            listCall.enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                    Log.d("Testing:::::::::::::::>", "onResponse");
                    if (response.isSuccessful()) {

                        recipeList = response.body();

                        dataBaseBaking.deleteAll();
                        dataBaseBaking.insert(recipeList);

                        recipeNames = new ArrayList<>();
                        for (int i = 0; i < recipeList.size(); i++) {

                            recipeNames.add(recipeList.get(i).getName());
                        }
                        recipesAdapter.setNames(recipeNames);
                        progressBar.setVisibility(View.GONE);
                    } else {

                        recipeList = dataBaseBaking.getRecipes();
                        if (recipeList.isEmpty()){

                            Toast.makeText(MainActivity.this, "No Data!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        } else {

                            recipeNames = new ArrayList<>();
                            for (int i = 0; i < recipeList.size(); i++) {

                                recipeNames.add(recipeList.get(i).getName());
                            }
                            recipesAdapter.setNames(recipeNames);
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                    countingIdlingResource.decrement();
                }

                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {

                    Log.d("Testing:::::::::::::::>", "onFailure" + t.getMessage());

                    countingIdlingResource.decrement();
                }
            });

        } else {

            progressBar.setVisibility(View.VISIBLE);
            recipeList = dataBaseBaking.getRecipes();

            if (recipeList.isEmpty()){

                Toast.makeText(MainActivity.this, "No Data!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else {

                recipeNames = new ArrayList<>();
                for (int i = 0; i < recipeList.size(); i++) {

                    recipeNames.add(recipeList.get(i).getName());
                }
                recipesAdapter.setNames(recipeNames);
                progressBar.setVisibility(View.GONE);
            }

            countingIdlingResource.decrement();
        }

    }

    @Override
    public void onRecipeClick(int position) {

        Recipe selectedRecipe = recipeList.get(position);
        Intent intent = new Intent(MainActivity.this, StepsActivity.class);
        intent.putExtra("selectedRecipe", selectedRecipe);
        startActivity(intent);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public CountingIdlingResource getIdlingResource() {
        return countingIdlingResource;
    }


}
