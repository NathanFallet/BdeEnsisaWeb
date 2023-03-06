<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="row">
      <div class="col-12">
        <div class="card card-body">
          <form action="/admin/uploads" class="form-control dropzone" id="dropzone">
            <div class="fallback">
              <input name="file" type="file" multiple />
            </div>
          </form>
        </div>
        <div class="card mt-4">
          <div class="table-responsive">
            <table class="table table-flush" id="datatable-search">
              <thead class="thead-light">
                <tr>
                  <th>Nom du fichier</th>
                </tr>
              </thead>
              <tbody>
                <#list uploads as upload>
                <tr>
                  <td class="font-weight-bold">
                    <span class="my-2 text-xs"><a href="/uploads/${upload.name}">/uploads/${upload.name}</a></span>
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
  <script src="https://unpkg.com/dropzone@5/dist/min/dropzone.min.js"></script>
  <script>
    const dataTableSearch = new simpleDatatables.DataTable("#datatable-search", {
      searchable: true,
      fixedHeight: false,
      perPageSelect: false
    });
    Dropzone.autoDiscover = false;
    var drop = document.getElementById('dropzone')
    var myDropzone = new Dropzone(drop, {
      url: "/admin/uploads",
      addRemoveLinks: true
    });
  </script>
</@template.page>
