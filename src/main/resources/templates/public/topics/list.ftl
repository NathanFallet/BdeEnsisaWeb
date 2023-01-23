<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="row mt-lg-4 mt-2">
        <#list topics as topic>
        <div class="col-lg-4 col-md-6 mb-4">
          <div class="card">
            <div class="card-body p-3">
                <h6 class="mb-0 px-3 pt-3">${topic.title}</h6>
                <p class="mb-0 px-3">Ajoutée ${topic.formatted}</p>
                <hr class="horizontal dark">
                <p class="mb-0 px-3 pb-3">${topic.content}</p>
            </div>
          </div>
        </div>
        </#list>
    </div>
</div>
</@template.page>
