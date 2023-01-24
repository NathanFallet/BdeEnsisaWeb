<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0">Poser une question</h6>
        <p class="text-sm mb-0">Poser une question au BDE.</p>
        <hr class="horizontal dark my-3">

        <#if success??>
        <div class="alert alert-success">
            Votre question a bien été posée. Le BDE y repondra bientôt.
        </div>
        </#if>
        <#if error??>
        <div class="alert alert-danger">
            Veuillez remplir tous les champs du formulaire.
        </div>
        </#if>

        <form method="post" id="form">
            <label for="content">Votre question</label>
            <textarea name="content" id="content" class="form-control"></textarea>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/questions">Annuler</a>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Poser">
            </div>
        </form>
    </div>
</div>
</@template.page>
