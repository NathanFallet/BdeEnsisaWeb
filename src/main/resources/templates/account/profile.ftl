<#import "../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="row mt-lg-4 mt-2">
        <div class="col-md-8 me-auto text-left">
            <h2 class="h5">Mon QR Code</h3>
        </div>
    </div>
    <div class="row mt-lg-4 mt-2">
        <div class="col-lg-4 col-md-6 mb-4">
          <div class="card">
            <div class="card-body p-3">
                <img class="img-fluid p-3" src="/account/qrcode" alt="Mon QR Code">
                <hr class="horizontal dark">
                <h6 class="mb-0 px-3">${user.firstName} ${user.lastName}</h6>
                <p class="mb-0 px-3">${user.description}</p>
                <hr class="horizontal dark">
                <#if cotisant??>
                <p class="text-success font-weight-bold mb-0 px-3">Cotisant</p>
                <p class="mb-0 px-3 pb-3">Expire : ${cotisant.formatted}</p>
                <#else>
                <p class="text-danger font-weight-bold mb-0 px-3 pb-3">Non cotisant</p>
                </#if>
            </div>
          </div>
        </div>
    </div>
</div>
</@template.page>
