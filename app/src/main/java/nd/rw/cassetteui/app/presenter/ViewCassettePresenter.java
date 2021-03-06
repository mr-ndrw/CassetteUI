package nd.rw.cassetteui.app.presenter;

import android.util.Log;

import nd.rw.cassetteui.app.model.CassetteModel;
import nd.rw.cassetteui.app.model.RecordingModel;
import nd.rw.cassetteui.app.presenter.dp.ListCassettePresenterSubject;
import nd.rw.cassetteui.app.view.DetailCassetteView;
import nd.rw.cassetteui.domain.usecase.ViewCassetteUseCase;

public class ViewCassettePresenter implements Presenter{

    //region Fields

    private static final String TAG = "ViewCasPres";
    private ViewCassetteUseCase useCase = new ViewCassetteUseCase();
    private DetailCassetteView view;

    private int cassetteId;
    private CassetteModel cassetteModel;

    //endregion Fields

    //region Constructors

    public ViewCassettePresenter(DetailCassetteView view) {
        this.view = view;
    }

    public ViewCassettePresenter() {
    }

    //endregion Constructors

    //region Methods

    public void initialize(int cassetteId){
        this.cassetteId = cassetteId;
        this.cassetteModel = useCase.getCassetteById(this.cassetteId);
        this.view.renderCassetteAndRecordings(this.cassetteModel);
    }

    public void setView(DetailCassetteView view) {
        if (view == null) {
            throw new RuntimeException("View shouldn't be null!");
        }
        this.view = view;
    }

    public boolean updateCassette(String title, String description){
        String oldTitle = cassetteModel.getTitle();
        String oldDesc = cassetteModel.getDescription();
        this.cassetteModel.setTitle(title);
        this.cassetteModel.setDescription(description);

        boolean updateWasSuccessful = this.useCase.updateCassette(cassetteModel);

        if(!updateWasSuccessful){
            this.cassetteModel.setTitle(oldTitle);
            this.cassetteModel.setDescription(oldDesc);
        } else {
            ListCassettePresenterSubject.getInstance().notifyAboutUpdatedCassette(cassetteModel);
        }
        this.view.refreshTitleAndDescription(this.cassetteModel);
        return updateWasSuccessful;
    }

    /**
     *  Deletes Cassette associated with this Presenter.
     *  @return Success of this operation.
     */
    public boolean deleteCassette(){
        boolean deleteWasSuccessful = useCase.deleteCassette(cassetteId);
        if (deleteWasSuccessful){
            ListCassettePresenterSubject.getInstance().notifyAboutDeletedCassette(cassetteModel);
        }
        return deleteWasSuccessful;
    }

    public boolean deleteRecording(RecordingModel recording){
        boolean recordingWasDeleted = useCase.deleteRecording(recording);
        if (recordingWasDeleted){
            Log.i(TAG, "deleteRecording: Delete was successful.");
            ListCassettePresenterSubject.getInstance().notifyAboutUpdatedCassette(this.cassetteModel);
            //  So that cassette list updates itself accordingly.
        }
        return recordingWasDeleted;
    }

    public void refreshTitleAndDescriptionInView(){
        this.view.refreshTitleAndDescription(this.cassetteModel);
    }

    //endregion Methods

    //region Presenter Methods

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    //endregion Presenter Methods
}
