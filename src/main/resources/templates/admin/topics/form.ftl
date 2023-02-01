<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0"><#if topic??>Modifier l'<#else>Nouvelle </#if>affaire</h6>
        <p class="text-sm mb-0"><#if topic??>Modifier une<#else>Créer une nouvelle</#if> affaire sur le site</p>
        <hr class="horizontal dark my-3">

        <form method="post" id="form">
            <label for="title" class="form-label">Titre de l'affaire</label>
            <input type="text" class="form-control" name="title" id="title" <#if topic??>value="${topic.title}"</#if>>

            <div class="form-group mt-4">
                <label for="home">
                    Affaire validée
                </label>
                <p class="form-text text-muted text-xs ms-1">
                    Si l'affaire est validée, elle sera affichée publiquement sur le site.<br/>
                    Une affaire proposée par un utilisateur n'est pas validée par défaut.
                </p>
                <div class="form-check form-switch ms-1">
                    <input class="form-check-input" type="checkbox" name="validated" id="validated" <#if topic?? && topic.validated>checked</#if>>
                    <label class="form-check-label" for="validated"></label>
                </div>
            </div>

            <label for="content" class="mt-4">Contenu de l'affaire</label>
            <p class="form-text text-muted text-xs ms-1">
                Ce qui sera affichée sur la page de l'affaire.
                Le formatage Markdown est supporté.
            </p>
            <textarea name="content" id="content" class="form-control"><#if topic??>${topic.content}</#if></textarea>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/admin/topics">Annuler</a>
                <#if topic??>
                <a class="btn btn-danger m-0 ms-2" href="/admin/topics/${topic.id}/delete">Supprimer</a>
                </#if>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="<#if topic??>Modifier<#else>Créer</#if>">
            </div>
        </form>
    </div>
</div>
</@template.page>
