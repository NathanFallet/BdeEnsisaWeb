<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <h1 class="d-none">Clubs</h1>
    <div class="d-sm-flex justify-content-between">
        <div>
            <a href="/clubs/suggest" class="btn btn-icon btn-outline-primary">
                Proposer un club
            </a>
        </div>
    </div>
    <#if error??>
    <div class="alert alert-danger text-white">
        ${error}
    </div>
    </#if>
    <#if mine?hasContent>
    <div class="row mt-lg-4 mt-2">
        <div class="col-md-8 me-auto text-left">
            <h2 class="h5">Mes clubs</h3>
        </div>
    </div>
    <div class="row mt-lg-4 mt-2">
        <#list mine as membership>
        <div class="col-lg-4 col-md-6 mb-4">
          <div class="card">
            <div class="card-body p-3">
                <h6 class="mb-0 px-3 pt-3">
                    <a href="/clubs/${membership.club.id}">${membership.club.name}</a>
                    <#if !membership.club.validated>
                    <span class="badge bg-gradient-warning text-white float-end">En attente</span>
                    <#elseIf membership.role == "admin">
                    <span class="badge bg-gradient-dark text-white float-end">Admin</span>
                    <#else>
                    <span class="badge bg-gradient-success text-white float-end">Membre</span>
                    </#if>
                </h6>
                <p class="mb-0 px-3">Ajouté ${membership.club.formatted}</p>
                <hr class="horizontal dark">
                <div class="mb-0 px-3">${membership.club.markdown}</div>
            </div>
          </div>
        </div>
        </#list>
    </div>
    <div class="row mt-lg-4 mt-2">
        <div class="col-md-8 me-auto text-left">
            <h2 class="h5">Autres clubs</h3>
        </div>
    </div>
    </#if>
    <div class="row mt-lg-4 mt-2">
        <#list clubs as club>
        <div class="col-lg-4 col-md-6 mb-4">
          <div class="card">
            <div class="card-body p-3">
                <h6 class="mb-0 px-3 pt-3">
                    <a href="/clubs/${club.id}">${club.name}</a>
                    <#if join>
                    <a href="/clubs/${club.id}/join" class="badge bg-gradient-info text-white float-end">Rejoindre</a>
                    </#if>
                </h6>
                <p class="mb-0 px-3">Ajouté ${club.formatted}</p>
                <hr class="horizontal dark">
                <div class="mb-0 px-3">${club.markdown}</div>
            </div>
          </div>
        </div>
        </#list>
    </div>
</div>
</@template.page>
