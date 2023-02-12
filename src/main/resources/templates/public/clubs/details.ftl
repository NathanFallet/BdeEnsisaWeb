<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <#if membership??>
    <div class="d-sm-flex justify-content-between">
        <div>
            <a href="/clubs/${club.id}/leave" class="btn btn-icon btn-outline-primary">
                Quitter le club
            </a>
            <#if membership.role == "admin">
            <a href="/clubs/${club.id}/edit" class="btn btn-icon btn-outline-primary">
                Modifier le club
            </a>
            </#if>
        </div>
    </div>
    </#if>
    <div class="card">
        <div class="card-body">
            <h1 class="mb-4">${club.name}</h1>
            ${club.markdown}
        </div>
    </div>
    <div class="card card-body mt-4">
        <h6 class="mb-0">Liste des membres du club</h6>
        <hr class="horizontal dark my-3">

        <div class="row">
            <#list members as member>
            <div class="col-lg-4 col-md-6">
                <p class="text-sm text-bold mb-0">
                    ${member.user.firstName} ${member.user.lastName}
                    <#if member.role == "admin">
                    <span class="badge bg-gradient-dark text-white float-end">Admin</span>
                    <#else>
                    <span class="badge bg-gradient-success text-white float-end">Membre</span>
                    </#if>
                </p>
                <p class="text-xs">${member.user.description}</p>
            </div>
            </#list>
        </div>
    </div>
</div>
</@template.page>
