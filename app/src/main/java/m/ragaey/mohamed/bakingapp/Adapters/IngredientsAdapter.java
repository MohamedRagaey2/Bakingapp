package m.ragaey.mohamed.bakingapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import m.ragaey.mohamed.bakingapp.Models.Ingredient;
import m.ragaey.mohamed.bakingapp.R;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private List<Ingredient> ingredients = null;
    private Context context = null;

    public IngredientsAdapter(List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new IngredientViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.ingredient_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder ingredientViewHolder, int position) {

        Ingredient ingredient = ingredients.get(position);

        ingredientViewHolder.ingredient_number.setText(String.valueOf(position + 1));
        ingredientViewHolder.ingredient_description.setText(ingredient.getIngredient());
        ingredientViewHolder.ingredient_measure.setText("Measure: ".concat(ingredient.getMeasure()));
        ingredientViewHolder.ingredient_quantity
                .setText("Quantity: ".concat(String.valueOf(ingredient.getQuantity())));
    }

    @Override
    public int getItemCount() {

        if (ingredients == null) {
            return 0;
        } else {
            return ingredients.size();
        }
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView ingredient_number, ingredient_description,
                ingredient_measure, ingredient_quantity;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            ingredient_number = itemView.findViewById(R.id.ingredient_number);
            ingredient_description = itemView.findViewById(R.id.ingredient_description);
            ingredient_measure = itemView.findViewById(R.id.ingredient_measure);
            ingredient_quantity = itemView.findViewById(R.id.ingredient_quantity);
        }

    }
}
