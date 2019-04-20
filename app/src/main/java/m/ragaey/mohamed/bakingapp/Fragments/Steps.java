package m.ragaey.mohamed.bakingapp.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;
import java.util.Objects;

import m.ragaey.mohamed.bakingapp.Adapters.StepsAdapter;
import m.ragaey.mohamed.bakingapp.Models.Recipe;
import m.ragaey.mohamed.bakingapp.Models.Step;
import m.ragaey.mohamed.bakingapp.R;

import static m.ragaey.mohamed.bakingapp.Activities.StepsActivity.twoPaneMode;

public class Steps extends Fragment implements StepsAdapter.OnStepClickListener {

    private Recipe recipe = null;
    private List<Step> stepList = null;

    private RecyclerView stepsRecyclerView = null;
    private StepsAdapter stepsAdapter = null;
    private OnFragmentInteractionListener mListener;

    public Steps() {
        // Required empty public constructor
    }

    public static Steps newInstance (Recipe recipe1){

        Steps steps = new Steps();
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", recipe1);
        steps.setArguments(bundle);
        return steps;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("recipe")) {
            recipe = (Recipe) bundle.getSerializable("recipe");
            stepList = recipe.getSteps();
            stepsAdapter = new StepsAdapter(stepList, getActivity(), this);

            stepsRecyclerView = view.findViewById(R.id.list_steps);
            stepsRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL));
            stepsRecyclerView.setAdapter(stepsAdapter);
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (twoPaneMode) {
            Details fragment = Details.newInstance(0, recipe);
            Objects.requireNonNull(getFragmentManager()).beginTransaction()
                    .replace(R.id.details_fragment, fragment).commit();
        }
    }

    @Override
    public void onStepClick(int position) {

        if (mListener != null){
            mListener.fragmentOnItemClick(position);
        }
    }

    public interface OnFragmentInteractionListener {

        void fragmentOnItemClick(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
