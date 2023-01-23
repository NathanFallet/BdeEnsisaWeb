<#import "../template.ftl" as template>
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
            <div class="row">
                <div class="col-sm-6 mb-4">
                    <div class="card">
                        <div class="card-body">
                            <p>Utilisateurs</p>
                            <h3>${counts.users}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 mb-4">
                    <div class="card">
                        <div class="card-body">
                            <p>Cotisants</p>
                            <h3>${counts.cotisants}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 mb-4">
                    <div class="card">
                        <div class="card-body">
                            <p>Pages</p>
                            <h3>${counts.pages}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 mb-4">
                    <div class="card">
                        <div class="card-body">
                            <p>Affaires</p>
                            <h3>${counts.topics}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 mb-4">
                    <div class="card">
                        <div class="card-body">
                            <p>Evènements</p>
                            <h3>-</h3>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 mb-4">
                    <div class="card">
                        <div class="card-body">
                            <p>Tickets</p>
                            <h3>-</h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-6">
            <div class="card">
                <div class="card-header pb-0 p-3">
                    <div class="row">
                        <div class="col-md-6">
                            <h6 class="mb-0">Dernières cotisations</h6>
                        </div>
                    </div>
                </div>
                <div class="card-body p-3">
                    <ul class="list-group">
                        <#list tables.cotisants as cotisant>
                        <li class="list-group-item border-0 justify-content-between px-0 pb-2 border-radius-lg">
                            <div class="d-flex">
                                <div class="d-flex align-items-center">
                                    <div class="d-flex flex-column">
                                        <h6 class="mb-1 text-dark text-sm">${cotisant.user.firstName} ${cotisant.user.lastName}</h6>
                                        <span class="text-xs">${cotisant.user.description}</span>
                                    </div>
                                </div>
                                <div class="d-flex align-items-center text-success text-gradient text-sm text-end font-weight-bold ms-auto">
                                    Jusqu'au :<br/>
                                    ${cotisant.formatted}
                                </div>
                            </div>
                        </li>
                        </#list>
                    </ul>
                </div>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card">
                <div class="card-header pb-0 p-3">
                    <div class="row">
                        <div class="col-md-6">
                            <h6 class="mb-0">Derniers tickets vendus</h6>
                        </div>
                    </div>
                </div>
                <div class="card-body p-3">
                    <ul class="list-group">
                        
                    </ul>
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
        // TODO: Load real events
        {
          title: 'Réunion BDE',
          start: '2023-01-24',
          end: '2023-01-24',
          className: 'bg-gradient-success'
        },
        {
          title: 'Réunion Gala',
          start: '2023-01-25',
          end: '2023-01-25',
          className: 'bg-gradient-warning'
        },
        {
          title: 'AG Budget',
          start: '2023-01-30',
          end: '2023-01-30',
          className: 'bg-gradient-danger'
        }
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