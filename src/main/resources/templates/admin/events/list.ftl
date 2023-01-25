<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="d-sm-flex justify-content-between">
      <div>
        <a href="/admin/events/new" class="btn btn-icon btn-outline-white">
          Nouvel évènement
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
                  <th>Date(s)</th>
                  <th>Affaire</th>
                </tr>
              </thead>
              <tbody>
                <#list events as event>
                <tr>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/admin/events/${event.id}">${event.id}</a></span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${event.title}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${event.formatted}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/admin/topics/${event.topic.id}">${event.topic.title}</a></span>
                  </td>
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
