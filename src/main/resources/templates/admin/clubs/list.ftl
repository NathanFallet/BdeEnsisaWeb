<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="d-sm-flex justify-content-between">
      <div>
        <a href="/admin/clubs/create" class="btn btn-icon btn-outline-primary">
          Nouveau club
        </a>
      </div>
    </div>
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="table-responsive">
            <table class="table table-flush" id="datatable-search">
              <thead class="thead-light">
                <tr>
                  <th>Id</th>
                  <th>Nom</th>
                  <th>Date d'ajout</th>
                  <th>Validé</th>
                </tr>
              </thead>
              <tbody>
                <#list clubs as club>
                <tr>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/admin/clubs/${club.id}">${club.id}</a></span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${club.name}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${club.formatted}</span>
                  </td>
                  <td class="text-xs font-weight-bold">
                    <div class="d-flex align-items-center">
                      <button class="btn btn-icon-only btn-rounded btn-outline-<#if club.validated>success<#else>danger</#if> mb-0 me-2 btn-sm d-flex align-items-center justify-content-center"><i class="fas fa-<#if club.validated>check<#else>times</#if>" aria-hidden="true"></i></button>
                      <span><#if club.validated>Oui<#else>Non</#if></span>
                    </div>
                  </td>
                </tr>
                </#list>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
  <script src="https://cdn.jsdelivr.net/npm/simple-datatables@3.0.2/dist/umd/simple-datatables.js"></script>
  <script>
    const dataTableSearch = new simpleDatatables.DataTable("#datatable-search", {
      searchable: true,
      fixedHeight: false,
      perPageSelect: false
    });
  </script>
</@template.page>
