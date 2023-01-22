<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0"><#if item??>Modifier l'<#else>Nouvel </#if>élément de menu</h6>
        <p class="text-sm mb-0"><#if item??>Modifier un<#else>Créer un nouvel</#if> élément sur le menu du site</p>
        <hr class="horizontal dark my-3">

        <form method="post">
            <label for="title" class="form-label">Titre de l'élément</label>
            <input type="text" class="form-control" name="title" id="title" <#if item??>value="${item.title}"</#if>>

            <label for="url" class="form-label mt-4">URL de l'élément</label>
            <input type="text" class="form-control" name="url" id="url" <#if item??>value="${item.url}"</#if>>

            <label for="position" class="form-label mt-4">Position de l'élément</label>
            <input type="number" class="form-control" name="position" id="position" <#if item??>value="${item.position}"</#if>>

            <label for="parent" class="form-label mt-4">Menu parent</label>
            <select class="form-control" name="parent" id="parent">
                <option value="">Aucun</option>
                <#list parents as parent>
                    <option value="${parent.id}" <#if item?? && item.parent?? && item.parent == parent.id>selected</#if>>${parent.title}</option>
                </#list>
            </select>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/admin/menu">Annuler</a>
                <#if item??>
                <a class="btn btn-danger m-0 ms-2" href="/admin/menu/${item.id}/delete">Supprimer</a>
                </#if>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="<#if item??>Modifier<#else>Créer</#if>">
            </div>
        </form>
    </div>
</div>
</@template.page>
