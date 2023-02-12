<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
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
                    <#if member.role == "owner">
                    <span class="badge bg-gradient-dark text-white float-end">Créateur</span>
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
