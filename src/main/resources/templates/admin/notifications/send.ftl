<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0">Envoyer une notification</h6>
        <p class="text-sm mb-0">Envoyer une notification aux utilisteurs de l'application mobile.</p>
        <hr class="horizontal dark my-3">

        <#if success??>
        <div class="alert alert-success">
            Votre notification vient d'être envoyée.
        </div>
        </#if>
        <#if error??>
        <div class="alert alert-danger text-white">
            ${error}
        </div>
        </#if>

        <form method="post" id="form">
            <label for="topic" class="form-label mt-4">Sujet</label>
            <select class="form-control" name="topic" id="topic">
                <option value="broadcast">Général</option>
                <option value="cotisants">Cotisants</option>
                <option value="events">Evènements</option>
            </select>

            <label for="title" class="form-label">Titre de la notification</label>
            <input type="text" class="form-control" name="title" id="title" required>

            <label for="body" class="mt-4">Contenu de la notification</label>
            <p class="form-text text-muted text-xs ms-1">
                Gardez un contenu court et concis.
            </p>
            <textarea name="body" id="body" class="form-control"></textarea>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/notifications">Annuler</a>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Envoyer">
            </div>
        </form>
    </div>
</div>
</@template.page>
