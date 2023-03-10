<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0"><#if page??>Modifier la<#else>Nouvelle</#if> page</h6>
        <p class="text-sm mb-0"><#if page??>Modifier une<#else>Créer une nouvelle</#if> page statique sur le site</p>
        <hr class="horizontal dark my-3">

        <form method="post" id="form">
            <label for="title" class="form-label">Titre de la page</label>
            <input type="text" class="form-control" name="title" id="title" <#if page??>value="${page.title}"</#if>>

            <label for="url" class="form-label mt-4">URL de la page</label>
            <div class="input-group mb-3">
                <span class="input-group-text" id="url-prefix">/pages/</span>
                <input type="text" class="form-control" name="url" id="url" aria-describedby="url-prefix" <#if page??>value="${page.url}"</#if>>
            </div>

            <div class="form-group mt-4">
                <label for="home">
                    Page d'accueil
                </label>
                <p class="form-text text-muted text-xs ms-1">
                    Si la page doit être affichée en tant que page par défaut.<br/>
                    Attention : une seule page par défaut peut être définie. Si plusieurs le sont, le comportement peut être inattendu.
                </p>
                <div class="form-check form-switch ms-1">
                    <input class="form-check-input" type="checkbox" name="home" id="home" <#if page?? && page.home>checked</#if>>
                    <label class="form-check-label" for="home"></label>
                </div>
            </div>

            <label for="content" class="mt-4">Contenu de la page</label>
            <p class="form-text text-muted text-xs ms-1">
                Ce qui sera affichée sur la page.
                Le formatage Markdown est supporté.
            </p>
            <textarea name="content" id="content" class="form-control" rows="7"><#if page??>${page.content}</#if></textarea>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/admin/pages">Annuler</a>
                <#if page??>
                <a class="btn btn-danger m-0 ms-2" href="/admin/pages/${page.id}/delete">Supprimer</a>
                </#if>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="<#if page??>Modifier<#else>Créer</#if>">
            </div>
        </form>
    </div>
</div>
</@template.page>
