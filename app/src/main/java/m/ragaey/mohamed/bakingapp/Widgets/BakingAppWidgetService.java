package m.ragaey.mohamed.bakingapp.Widgets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import m.ragaey.mohamed.bakingapp.Database.DataBaseBaking;
import m.ragaey.mohamed.bakingapp.Models.Ingredient;
import m.ragaey.mohamed.bakingapp.R;

import static m.ragaey.mohamed.bakingapp.Widgets.BakingAppWidgetConfig.KEY;

public class BakingAppWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingAppWidgetFactory(getApplicationContext());
    }

    public class BakingAppWidgetFactory implements RemoteViewsFactory {

        private Context context;
        private List<Ingredient> listOfIngredients;
        private DataBaseBaking dataBaseBaking;
        private int id;

        public BakingAppWidgetFactory(Context context) {
            this.context = context;
            this.dataBaseBaking = new DataBaseBaking(context);
            SharedPreferences sharedPreferences = context.getSharedPreferences("pref", MODE_PRIVATE);
            id = sharedPreferences.getInt(KEY, 1);
        }

        @Override
        public void onCreate() {
            listOfIngredients = dataBaseBaking.getIngredients(id);
        }

        @Override
        public void onDataSetChanged() {
            listOfIngredients.clear();
            listOfIngredients = dataBaseBaking.getIngredients(id);
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return listOfIngredients.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.baking_list_item_widget);
            remoteViews.setTextViewText(R.id.widget_item_description, listOfIngredients.get(i).getIngredient());
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
