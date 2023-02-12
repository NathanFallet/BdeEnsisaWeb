<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="d-sm-flex justify-content-between">
      <div>
        <a href="/admin/pages/new" class="btn btn-icon btn-outline-primary">
          Nouvelle page
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
                  <th>URL</th>
                  <th>Titre</th>
                  <th>Page d'accueil</th>
                </tr>
              </thead>
              <tbody>
                <#list pages as page>
                <tr>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/admin/pages/${page.id}">${page.id}</a></span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/pages/${page.url}">/pages/${page.url}</a></span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${page.title}</span>
                  </td>
                  <td class="text-xs font-weight-bold">
                    <div class="d-flex align-items-center">
                      <button class="btn btn-icon-only btn-rounded btn-outline-<#if page.home>success<#else>danger</#if> mb-0 me-2 btn-sm d-flex align-items-center justify-content-center"><i class="fas fa-<#if page.home>check<#else>times</#if>" aria-hidden="true"></i></button>
                      <span><#if page.home>Oui<#else>Non</#if></span>
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
