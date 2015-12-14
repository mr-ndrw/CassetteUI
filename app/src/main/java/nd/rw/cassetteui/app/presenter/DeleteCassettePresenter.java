package nd.rw.cassetteui.app.presenter;

import nd.rw.cassetteui.app.model.CassetteModel;
import nd.rw.cassetteui.domain.usecase.DeleteCassetteUseCase;

public class DeleteCassettePresenter {

    private DeleteCassetteUseCase useCase = new DeleteCassetteUseCase();

    public boolean deleteCassette(int id){
        return useCase.deleteCassetteModel(id) != null;
    }

    public boolean deleteCassette(CassetteModel cassetteModel) {
        return cassetteModel != null && this.deleteCassette(cassetteModel.getId());
    }
}