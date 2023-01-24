<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="d-sm-flex justify-content-between">
      <div>
        <a href="/admin/questions/new" class="btn btn-icon btn-outline-white">
          Nouvelle question
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
                  <th>Question</th>
                  <th>Ajoutée par</th>
                  <th>Date d'ajout</th>
                  <th>Répondue</th>
                </tr>
              </thead>
              <tbody>
                <#list questions as question>
                <tr>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/admin/questions/${question.id}">${question.id}</a></span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${question.content}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${question.user.firstName} ${question.user.lastName}</span>
                  </td>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs">${question.formatted}</span>
                  </td>
                  <td class="text-xs font-weight-bold">
                    <div class="d-flex align-items-center">
                      <button class="btn btn-icon-only btn-rounded btn-outline-<#if question.answer??>success<#else>danger</#if> mb-0 me-2 btn-sm d-flex align-items-center justify-content-center"><i class="fas fa-<#if question.answer??>check<#else>times</#if>" aria-hidden="true"></i></button>
                      <span><#if question.answer??>Oui<#else>Non</#if></span>
                    </div>
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
