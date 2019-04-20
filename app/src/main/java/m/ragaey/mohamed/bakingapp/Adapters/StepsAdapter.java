package m.ragaey.mohamed.bakingapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import m.ragaey.mohamed.bakingapp.Models.Step;
import m.ragaey.mohamed.bakingapp.R;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

    private List<Step> steps = null;
    private Context context = null;
    private OnStepClickListener onStepClickListener = null;


    public StepsAdapter(List<Step> steps, Context context, OnStepClickListener onStepClickListener) {
        this.steps = steps;
        this.context = context;
        this.onStepClickListener = onStepClickListener;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StepViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.steps_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder stepViewHolder, int position) {

        Step step = steps.get(position);

        stepViewHolder.step_number.setText(String.valueOf(step.getId()));
        stepViewHolder.step_short_description.setText(step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if (steps == null) {
            return 0;
        } else {
            return steps.size();
        }
    }

    public interface OnStepClickListener {

        void onStepClick(int position);
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView step_number = null;
        TextView step_short_description = null;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);

            step_number = itemView.findViewById(R.id.step_number);
            step_short_description = itemView.findViewById(R.id.step_short_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            onStepClickListener.onStepClick(getAdapterPosition());
        }
    }
}
