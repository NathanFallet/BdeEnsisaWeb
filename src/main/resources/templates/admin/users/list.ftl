<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="table-responsive">
            <table class="table table-flush" id="datatable-search">
              <thead class="thead-light">
                <tr>
                  <th>Id</th>
                  <th>Prénom</th>
                  <th>Nom</th>
                  <th>Email</th>
                  <th>Année/Option</th>
                  <th>Cotisant</th>
                  <th>Permissions</th>
                </tr>
              </thead>
              <tbody>
                <#list users as user>
                <tr>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/admin/users/${user.id}">${user.id}</a></span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${user.firstName}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${user.lastName}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${user.email}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${user.description}</span>
                  </td>
                  <td class="text-xs font-weight-bold">
                    <div class="d-flex align-items-center">
                      <button class="btn btn-icon-only btn-rounded btn-outline-<#if user.cotisant??>success<#else>danger</#if> mb-0 me-2 btn-sm d-flex align-items-center justify-content-center"><i class="fas fa-<#if user.cotisant??>check<#else>times</#if>" aria-hidden="true"></i></button>
                      <span><#if user.cotisant??>Oui<#else>Non</#if></span>
                    </div>
                  </td>
                  <td class="text-xs font-weight-bold">
                    <div class="d-flex align-items-center">
                      <button class="btn btn-icon-only btn-rounded btn-outline-<#if user.hasPermissions>success<#else>danger</#if> mb-0 me-2 btn-sm d-flex align-items-center justify-content-center"><i class="fas fa-<#if user.hasPermissions>check<#else>times</#if>" aria-hidden="true"></i></button>
                      <span><#if user.hasPermissions>Oui<#else>Non</#if></span>
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
