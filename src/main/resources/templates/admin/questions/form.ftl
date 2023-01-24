<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0"><#if question??>Modifier la<#else>Nouvelle</#if> question</h6>
        <p class="text-sm mb-0"><#if question??>Modifier une<#else>Créer une nouvelle</#if> question sur le site</p>
        <hr class="horizontal dark my-3">

        <form method="post" id="form">
            <label for="content">Votre question</label>
            <textarea name="content" id="content" class="form-control"><#if question??>${question.content}</#if></textarea>

            <label for="answer" class="mt-4">Réponse</label>
            <textarea name="answer" id="answer" class="form-control"><#if question?? && question.answer??>${question.answer}</#if></textarea>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/admin/questions">Annuler</a>
                <#if question??>
                <a class="btn btn-danger m-0 ms-2" href="/admin/questions/${question.id}/delete">Supprimer</a>
                </#if>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="<#if question??>Modifier<#else>Créer</#if>">
            </div>
        </form>
    </div>
</div>
</@template.page>
