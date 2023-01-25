<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="row">
        <div class="col-xl-8 col-lg-7 mb-4">
            <div class="card card-calendar">
                <div class="card-body p-3">
                  <div class="calendar" data-bs-toggle="calendar" id="calendar"></div>
                </div>
            </div>
        </div>
        <div class="col-xl-4 col-lg-5">
            <div class="card">
                <div class="card-header p-3 pb-0">
                  <h6 class="mb-0">A venir</h6>
                </div>
                <div class="card-body border-radius-lg p-3 pt-0">
                    <#list calendar as event>
                    <div class="d-flex mt-4">
                        <div class="numbers">
                            <h6 class="mb-1 text-dark text-sm">${event.title}</h6>
                            <span class="text-sm">${event.formatted}</span>
                        </div>
                    </div>
                    </#list>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="/js/plugins/fullcalendar.min.js"></script>
<script>
    var calendar = new FullCalendar.Calendar(document.getElementById("calendar"), {
      contentHeight: 'auto',
      initialView: "dayGridMonth",
      headerToolbar: {
        start: 'title', // will normally be on the left. if RTL, will be on the right
        center: '',
        end: 'today prev,next' // will normally be on the right. if RTL, will be on the left
      },
      selectable: true,
      editable: true,
      events: [
        <#list calendar as event>
        {
          title: '${event.title}',
          start: '${event.start}',
          end: '${event.end}',
          className: 'bg-gradient-primary'
        },
        </#list>
      ],
      views: {
        month: {
          titleFormat: {
            month: "long",
            year: "numeric"
          }
        },
        agendaWeek: {
          titleFormat: {
            month: "long",
            year: "numeric",
            day: "numeric"
          }
        },
        agendaDay: {
          titleFormat: {
            month: "short",
            year: "numeric",
            day: "numeric"
          }
        }
      },
    });

    calendar.render();
</script>
</@template.page>
