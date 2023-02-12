<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0">Proposer un club</h6>
        <p class="text-sm mb-0">Proposer un club, qui sera validé par le BDE avant d'être créé.</p>
        <hr class="horizontal dark my-3">

        <#if success??>
        <div class="alert alert-success">
            Votre club a bien été suggéré. Il sera validé par le BDE avant d'être créé.
        </div>
        </#if>
        <#if error??>
        <div class="alert alert-danger text-white">
            ${error}
        </div>
        </#if>

        <form method="post" id="form">
            <label for="name" class="form-label">Nom du club</label>
            <input type="text" class="form-control" name="name" id="name" required>

            <label for="description" class="mt-4">Description du club</label>
            <p class="form-text text-muted text-xs ms-1">
                Elle sera affichée sur la page des clubs.
            </p>
            <textarea name="description" id="description" class="form-control"></textarea>

            <label for="information" class="mt-4">Informations sur le club</label>
            <p class="form-text text-muted text-xs ms-1">
                Ce champ est à destination du BDE.<br/>
                Précisez : budget prévisionnel, demande de subvention, demande de local, demande de matériel, etc...
            </p>
            <textarea name="information" id="information" class="form-control"></textarea>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/clubs">Annuler</a>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Proposer">
            </div>
        </form>
    </div>
</div>
</@template.page>
