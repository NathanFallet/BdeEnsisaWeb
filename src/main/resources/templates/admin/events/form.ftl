<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0"><#if event??>Modifier l'<#else>Nouvel </#if>évènement</h6>
        <p class="text-sm mb-0"><#if event??>Modifier un<#else>Créer un nouvel</#if> évènement sur le site</p>
        <hr class="horizontal dark my-3">

        <form method="post" id="form">
            <label for="title" class="form-label">Titre de l'évènement</label>
            <input type="text" class="form-control" name="title" id="title" <#if event??>value="${event.title}"</#if>>

            <label for="content" class="mt-4">Contenu de l'évènement</label>
            <p class="form-text text-muted text-xs ms-1">
                Ce qui sera affichée sur la page de l'évènement
            </p>
            <textarea name="content" id="content" class="form-control"><#if event??>${event.content}</#if></textarea>

            <label for="start" class="form-label mt-4">Date de début</label>
            <input class="form-control datetimepicker" type="text" placeholder="Date de début" name="start" id="start" <#if event??>value="${event.start}"</#if> data-input>

            <label for="end" class="form-label mt-4">Date de fin</label>
            <input class="form-control datetimepicker" type="text" placeholder="Date de début" name="end" id="end" <#if event??>value="${event.end}"</#if> data-input>
            
            <label for="topic" class="form-label mt-4">Lié à l'affaire</label>
            <select class="form-control" name="topic" id="topic">
                <#list topics as topic>
                    <option value="${topic.id}" <#if event?? && event.topicId?? && event.topicId == topic.id>selected</#if>>${topic.title}</option>
                </#list>
            </select>

            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/admin/events">Annuler</a>
                <#if event??>
                <a class="btn btn-danger m-0 ms-2" href="/admin/events/${event.id}/delete">Supprimer</a>
                </#if>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="<#if event??>Modifier<#else>Créer</#if>">
            </div>
        </form>
    </div>
</div>
<script src="/js/plugins/flatpickr.min.js"></script>
<script>
    if (document.querySelector('.datetimepicker')) {
      flatpickr('.datetimepicker', {
        allowInput: true,
        enableTime: true,
        dateFormat: "Z",
        altInput: true,
        altFormat: "d/m/Y à H:i",
      }); // flatpickr
    }
</script>
</@template.page>
