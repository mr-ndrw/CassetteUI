package nd.rw.cassetteui.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import nd.rw.cassetteui.R;
import nd.rw.cassetteui.app.listeners.OnCassetteClickedHandler;
import nd.rw.cassetteui.app.model.CassetteModel;
import nd.rw.cassetteui.app.presenter.ListCassettePresenter;
import nd.rw.cassetteui.app.view.ListCassettesView;
import nd.rw.cassetteui.app.view.activity.AddCassetteActivity;
import nd.rw.cassetteui.app.view.activity.DetailCassetteActivity;
import nd.rw.cassetteui.app.view.adapter.CassetteLayoutManager;
import nd.rw.cassetteui.app.view.adapter.CassettesAdapter;
import nd.rw.cassetteui.app.view.decoration.DividerItemDecoration;

public class ListCassetteFragment
        extends BaseFragment
        implements ListCassettesView, OnCassetteClickedHandler{

    //region Fields

    public static final String TAG = "LI_CAS_FRAG";
    public static final int LIST_FRAGMENT_ADD_ACTIVITY_REQUEST_CODE = 1;


    @Bind(R.id.rv_cassettes)
    public RecyclerView rv_cassettes;
    @Bind(R.id.layout_cassettes)
    public CoordinatorLayout cl_layout;
    @Bind(R.id.fab_addCassette)
    public FloatingActionButton fab_addCassette;

    public ListCassettePresenter presenter;

    private CassettesAdapter cassettesAdapter;

    private CassetteLayoutManager layoutManager;

    private OnCassetteClickedHandler onCassetteClickedHandler;


    //endregion Fields

    //region Constructor

    public ListCassetteFragment(){

    }

    //endregion Constructor

    //region Private Methods

    private void setupUI(){
        this.layoutManager = new CassetteLayoutManager(this.getContext());
        this.rv_cassettes.setLayoutManager(layoutManager);
        this.onCassetteClickedHandler = this;
        this.cassettesAdapter = new CassettesAdapter(new ArrayList<CassetteModel>(), onCassetteClickedHandler);
        this.rv_cassettes.setAdapter(cassettesAdapter);
        this.fab_addCassette.setOnClickListener(fabAddListener);
        this.rv_cassettes.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    //endregion Private Methods

    //region ListCassetteView implemented methods

    @Override
    public void renderCassetteList(Collection<CassetteModel> cassetteModelCollection) {
        Log.d(TAG, "renderCassetteList");
        if (cassetteModelCollection == null) {
            throw new RuntimeException("Cassette model collection should not be null.");
        }
        this.cassettesAdapter.setCassetteModelList(cassetteModelCollection);
    }

    @Override
    public void viewCassette(CassetteModel cassetteModel) {
    }

    @Override
    public void setOnCassetteClicked(OnCassetteClickedHandler onCassetteClickedHandler) {
        this.onCassetteClickedHandler = this;
    }

    @Override
    public void onAddedCassette(CassetteModel cassetteModel) {
        this.cassettesAdapter.addCassetteToTop(cassetteModel);
    }

    @Override
    public void onUpdatedCassette(CassetteModel cassetteModel) {
        this.cassettesAdapter.updateCassette(cassetteModel);
    }

    @Override
    public void onDeleteCassette(int cassetteId) {

    }

    //endregion ListCassetteView implemented methods

    //region LoadDataView implemented methods

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showRetry() {

    }

    @Override
    public void hideRetry() {

    }

    @Override
    public void showError(String message) {

    }

    //endregion LoadDataView implemented methods

    //region Fragment overridden methods

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_section_cassettes, container, false);
        ButterKnife.bind(this, view);
        this.setupUI();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "OnActivityCreated");
        super.onActivityCreated(savedInstanceState);

        //  why exactly is presenter instantiated here?
        //  could it be done elsewhere?
        this.presenter = new ListCassettePresenter(this);
        this.presenter.initialize();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        // FIXME: 18.12.2015 Clean this mess
        if (requestCode == LIST_FRAGMENT_ADD_ACTIVITY_REQUEST_CODE){

            if ((resultCode == AddCassetteActivity.ADD_ACTIVITY_LIST_FRAGMENT_RESULT_CODE) &&
                    (data.getBooleanExtra(AddCassetteActivity.INTENT_EXTRA_PARAM__WAS_ADDED, false))){
                Log.d(TAG, "onActivityResult: Recevieved result for added cassette");
                int newCassetteId = data.getIntExtra(AddCassetteActivity.INTENT_EXTRA_PARAM_NEW_CASSETTE_ID, -1);
                this.presenter.queryForNewlyAddedCassette(newCassetteId);
            } else if (resultCode == DetailCassetteActivity.DETAIL_ACTIVITY_DELETE_RESULT_CODE){
                Log.d(TAG, "onActivityResult: Received result for deleted cassette");
                int deleteCassetteId = data.getIntExtra(DetailCassetteActivity.INTENT_EXTRA_PARAM_CASSETTE_ID, -1);
                this.cassettesAdapter.deleteCassette(deleteCassetteId);
            }
        }
    }

    //endregion Fragment overridden methods

    //region Listeners and Events

    private View.OnClickListener fabAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = AddCassetteActivity.getCallingIntent(ListCassetteFragment.this.getContext());
            startActivityForResult(intent, LIST_FRAGMENT_ADD_ACTIVITY_REQUEST_CODE);
        }
    };

    //endregion Listeners and Events

    //region Static Methods

    public static ListCassetteFragment newInstance() {
        Bundle args = new Bundle();

        ListCassetteFragment fragment = new ListCassetteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCassetteClicked(CassetteModel cassetteModel, View cassetteViewForTransition) {
        Log.i(TAG, "onCassetteClicked");
        Intent intent = DetailCassetteActivity.getCallingIntent(this.getContext(), cassetteModel.getId());
        startActivityForResult(intent, LIST_FRAGMENT_ADD_ACTIVITY_REQUEST_CODE);
    }

    //endregion Static Methods
}
