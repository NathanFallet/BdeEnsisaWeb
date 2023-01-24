<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0">Suggérer une affaire</h6>
        <p class="text-sm mb-0">Suggérer une affaire sur le site, qui sera validée par le BDE avant d'être publiée.</p>
        <hr class="horizontal dark my-3">

        <#if success??>
        <div class="alert alert-success">
            Votre affaire a bien été suggérée. Elle sera validée par le BDE avant d'être publiée.
        </div>
        </#if>
        <#if error??>
        <div class="alert alert-danger">
            Veuillez remplir tous les champs du formulaire.
        </div>
        </#if>

        <form method="post" id="form">
            <label for="title" class="form-label">Titre de l'affaire</label>
            <input type="text" class="form-control" name="title" id="title" required>

            <label for="content" class="mt-4">Contenu de l'affaire</label>
            <p class="form-text text-muted text-xs ms-1">
                Ce qui sera affichée sur la page de l'affaire
            </p>
            <textarea name="content" id="content" class="form-control"></textarea>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/topics">Annuler</a>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Suggérer">
            </div>
        </form>
    </div>
</div>
</@template.page>
