<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="card card-body mt-4">
        <h6 class="mb-0">Modifier le club</h6>
        <hr class="horizontal dark my-3">

        <form method="post" id="form">
            <label for="name" class="form-label">Nom du club</label>
            <input type="text" class="form-control" name="name" id="name" value="${club.name}">

            <label for="description" class="mt-4">Description du club</label>
            <p class="form-text text-muted text-xs ms-1">
                Elle sera affichée sur la page des clubs.
            </p>
            <textarea name="description" id="description" class="form-control">${club.description}</textarea>

            <label for="information" class="mt-4">Informations sur le club</label>
            <p class="form-text text-muted text-xs ms-1">
                Ce champ est à destination du BDE.<br/>
                Précisez : budget prévisionnel, demande de subvention, demande de local, demande de matériel, etc...
            </p>
            <textarea name="information" id="information" class="form-control">${club.information}</textarea>
            
            <div class="d-flex justify-content-end mt-4">
                <a class="btn btn-light m-0" href="/clubs/${club.id}">Annuler</a>
                <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Modifier">
            </div>
        </form>
    </div>
    <div class="card card-body mt-4">
        <h6 class="mb-0">Liste des membres du club</h6>
        <hr class="horizontal dark my-3">

        <div class="row">
            <#list members as member>
            <div class="col-lg-4 col-md-6">
                <div class="dropdown float-end">
                    <#if member.role == "admin">
                    <a href="#" class="badge bg-gradient-dark text-white dropdown-toggle" data-bs-toggle="dropdown" id="role-${member.user.id}">Admin</a>
                    <#else>
                    <a href="#" class="badge bg-gradient-success text-white dropdown-toggle" data-bs-toggle="dropdown" id="role-${member.user.id}">Membre</a>
                    </#if>
                    <ul class="dropdown-menu" aria-labelledby="role-${member.user.id}">
                        <li><a class="dropdown-item" href="/clubs/${club.id}/members/${member.user.id}/role/admin">Admin</a></li>
                        <li><a class="dropdown-item" href="/clubs/${club.id}/members/${member.user.id}/role/member">Membre</a></li>
                        <li><a class="dropdown-item" href="/clubs/${club.id}/members/${member.user.id}/role/remove">Retirer</a></li>
                    </ul>
                </div>
                <p class="text-sm text-bold mb-0">${member.user.firstName} ${member.user.lastName}</p>
                <p class="text-xs">${member.user.description}</p>
            </div>
            </#list>
        </div>
    </div>
</div>
</@template.page>
