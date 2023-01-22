<#import "../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="row">
        <div class="col-md-3">
            <div class="card">
                <div class="card-body text-center">
                    <h1 class="text-gradient text-primary"><span id="status1" countto="${counts.users}">${counts.users}</span></h1>
                    <h6 class="mb-0 font-weight-bolder">utilisateurs</h6>
                    <p class="opacity-8 mb-0 text-sm">inscrits sur le site</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body text-center">
                    <h1 class="text-gradient text-primary"><span id="status1" countto="${counts.cotisants}">${counts.cotisants}</span></h1>
                    <h6 class="mb-0 font-weight-bolder">cotisants</h6>
                    <p class="opacity-8 mb-0 text-sm">inscrits sur le site</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body text-center">
                    <h1 class="text-gradient text-primary"><span id="status1" countto="${counts.pages}">${counts.pages}</span></h1>
                    <h6 class="mb-0 font-weight-bolder">pages</h6>
                    <p class="opacity-8 mb-0 text-sm">disponible sur le site</p>
                </div>
            </div>
        </div>
    </div>
</div>
</@template.page>