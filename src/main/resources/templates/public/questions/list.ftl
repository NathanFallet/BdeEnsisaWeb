<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="d-sm-flex justify-content-between">
        <div>
            <a href="/questions/ask" class="btn btn-icon btn-outline-white">
                Poser une question
            </a>
        </div>
    </div>
    <div class="row mt-lg-4 mt-2">
        <#list questions as question>
        <div class="col-lg-4 col-md-6 mb-4">
          <div class="card">
            <div class="card-body p-3">
                <h6 class="mb-0 px-3 pt-3">${question.content}</h6>
                <p class="mb-0 px-3">Posée ${question.formatted}</p>
                <hr class="horizontal dark">
                <#if question.answer??>
                <p class="text-success font-weight-bold mb-0 px-3 pb-3">${question.answer}</p>
                <#else>
                <p class="text-danger font-weight-bold mb-0 px-3 pb-3">Pas encore répondue</p>
                </#if>
            </div>
          </div>
        </div>
        </#list>
    </div>
</div>
</@template.page>
