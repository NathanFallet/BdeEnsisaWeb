<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="d-sm-flex justify-content-between">
      <div>
        <a href="/admin/menu/new" class="btn btn-icon btn-outline-white">
          Nouvel élément de menu
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
                  <th>Titre</th>
                  <th>URL</th>
                  <th>Parent</th>
                </tr>
              </thead>
              <tbody>
                <#list menuitems as item>
                <tr>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/admin/menu/${item.id}">${item.id}</a></span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${item.title}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="${item.url}">${item.url}</a></span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><#if item.parent??><a href="/admin/menu/${item.parent}">${item.parent}</a><#else>Pas de parent</#if></span>
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
  <script src="/js/plugins/dragula/dragula.min.js"></script>
  <script src="/js/plugins/jkanban/jkanban.js"></script>
  <script src="/js/plugins/datatables.js"></script>
  <script>
    const dataTableSearch = new simpleDatatables.DataTable("#datatable-search", {
      searchable: true,
      fixedHeight: false,
      perPageSelect: false
    });
  </script>
</@template.page>
