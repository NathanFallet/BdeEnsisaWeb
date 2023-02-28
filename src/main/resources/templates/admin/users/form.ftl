<#import "../../template.ftl" as template>
<@template.page>
<div class="container-fluid py-4">
    <div class="row">
        <div class="col-md-8">
            <div class="card card-body mt-4">
                <h6 class="mb-0">Profil de l'utilisateur</h6>
                <p class="text-sm mb-0">Visualiser les informations d'un utilisateur du site</p>
                <hr class="horizontal dark my-3">

                <form method="post">
                    <div class="row">
                        <div class="col-6">
                            <label for="first_name" class="form-label">Prénom</label>
                            <input type="text" class="form-control" name="first_name" id="first_name" value="${user.firstName}">
                        </div>
                        <div class="col-6">
                            <label for="last_name" class="form-label">Nom</label>
                            <input type="text" class="form-control" name="last_name" id="last_name" value="${user.lastName}">
                        </div>
                    </div>

                    <label for="email" class="form-label mt-4">Email</label>
                    <input type="text" class="form-control" name="email" id="email" value="${user.email}" disabled>

                    <div class="row">
                        <div class="col-4">
                            <label for="year" class="form-label mt-4">Année</label>
                            <select name="year" class="form-control" id="year">
                                <option value="CPB" <#if user.year == "CPB">selected</#if>>CPB</option>
                                <option value="1A" <#if user.year == "1A">selected</#if>>1A</option>
                                <option value="2A" <#if user.year == "2A">selected</#if>>2A</option>
                                <option value="3A" <#if user.year == "3A">selected</#if>>3A</option>
                                <option value="other" <#if user.year == "other">selected</#if>>4A et plus</option>
                            </select>
                        </div>
                        <div class="col-8">
                            <label for="option" class="form-label mt-4">Option</label>
                            <select name="option" class="form-control" id="option">
                                <option value="ir" <#if user.option == "ir">selected</#if>>Informatique et Réseaux</option>
                                <option value="ase" <#if user.option == "ase">selected</#if>>Automatique et Systèmes embarqués</option>
                                <option value="meca" <#if user.option == "meca">selected</#if>>Mécanique</option>
                                <option value="tf" <#if user.option == "tf">selected</#if>>Textile et Fibres</option>
                                <option value="gi" <#if user.option == "gi">selected</#if>>Génie Industriel</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="d-flex justify-content-end mt-4">
                        <a class="btn btn-light m-0" href="/admin/users">Annuler</a>
                        <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Modifier">
                    </div>
                </form>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card card-body mt-4">
                <h6 class="mb-0">Statut de l'utilisateur</h6>
                <hr class="horizontal dark my-3">

                <#if user.cotisant??>
                <div class="alert alert-success">
                    Cet utilisateur est cotisant jusqu'au ${user.cotisant.formatted}.
                </div>
                <#else>
                <div class="alert alert-danger text-white">
                    Cet utilisateur n'est pas cotisant.
                </div>
                </#if>

                <hr class="horizontal dark my-3">

                <form method="post">
                    <label for="expiration" class="form-label">Définir le statut de cotisant jusqu'au :</label>
                    <input class="form-control datetimepicker" type="text" placeholder="Date d'expiration" name="expiration" id="expiration" data-input>

                    <div class="d-flex justify-content-end mt-4">
                        <button class="btn btn-light m-0" type="button" onclick="oneYear();">1 an</button>
                        <button class="btn btn-light m-0 ms-2" type="button" onclick="fiveYears();">Scolarité</button>
                        <input type="submit" class="btn bg-gradient-primary m-0 ms-2" value="Définir">
                    </div>
                </form>
            </div>
        </div>
        <#if permissions>
        <div class="col-12">
            <div class="card card-body mt-4">
                <h6 class="mb-0">Permissions de l'utilisateur</h6>
                <hr class="horizontal dark my-3">

                <form method="post">
                    <input type="checkbox" id="admin.*" name="admin.*" />
                    <label for="admin.*">admin.*</label>
                    <span class="small">: Toutes les permissions</span>
                    <div class="ms-3">
                        <input type="checkbox" id="admin.dashboard" name="admin.dashboard" />
                        <label for="admin.dashboard">admin.dashboard</label>
                        <span class="small">: Voir le tableau de bord</span>
                    </div>
                    <div class="ms-3">
                        <input type="checkbox" id="admin.notifications" name="admin.notifications" />
                        <label for="admin.notifications">admin.notifications</label>
                        <span class="small">: Envoyer des notifications</span>
                    </div>
                    <div class="row">
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.users.*" name="admin.users.*" />
                                <label for="admin.users.*">admin.users.*</label>
                                <span class="small">: Tout sur les utilisateurs</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.users.view" name="admin.users.view" />
                                    <label for="admin.users.view">admin.users.view</label>
                                    <span class="small">: Voir les utilisateurs</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.users.edit" name="admin.users.edit" />
                                    <label for="admin.users.edit">admin.users.edit</label>
                                    <span class="small">: Modifier les utilisateurs</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.permissions.*" name="admin.permissions.*" />
                                <label for="admin.permissions.*">admin.permissions.*</label>
                                <span class="small">: Tout sur les permissions</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.permissions.view" name="admin.permissions.view" />
                                    <label for="admin.permissions.view">admin.permissions.view</label>
                                    <span class="small">: Voir les permissions</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.permissions.edit" name="admin.permissions.edit" />
                                    <label for="admin.permissions.edit">admin.permissions.edit</label>
                                    <span class="small">: Modifier les permissions</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.uploads.*" name="admin.uploads.*" />
                                <label for="admin.uploads.*">admin.uploads.*</label>
                                <span class="small">: Tout sur les téléchargements</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.uploads.view" name="admin.uploads.view" />
                                    <label for="admin.uploads.view">admin.uploads.view</label>
                                    <span class="small">: Voir les téléchargements</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.uploads.create" name="admin.uploads.create" />
                                    <label for="admin.uploads.create">admin.uploads.create</label>
                                    <span class="small">: Créer des téléchargements</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.menu.*" name="admin.menu.*" />
                                <label for="admin.menu.*">admin.menu.*</label>
                                <span class="small">: Tout sur le menu</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.menu.view" name="admin.menu.view" />
                                    <label for="admin.menu.view">admin.menu.view</label>
                                    <span class="small">: Voir les éléments du menu</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.menu.create" name="admin.menu.create" />
                                    <label for="admin.menu.create">admin.menu.create</label>
                                    <span class="small">: Créer des éléments de menu</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.menu.edit" name="admin.menu.edit" />
                                    <label for="admin.menu.edit">admin.menu.edit</label>
                                    <span class="small">: Modifier les éléments du menu</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.menu.delete" name="admin.menu.delete" />
                                    <label for="admin.menu.delete">admin.menu.delete</label>
                                    <span class="small">: Supprimer les éléments du menu</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.pages.*" name="admin.pages.*" />
                                <label for="admin.pages.*">admin.pages.*</label>
                                <span class="small">: Tout sur les pages</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.pages.view" name="admin.pages.view" />
                                    <label for="admin.pages.view">admin.pages.view</label>
                                    <span class="small">: Voir les pages</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.pages.create" name="admin.pages.create" />
                                    <label for="admin.pages.create">admin.pages.create</label>
                                    <span class="small">: Créer des pages</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.pages.edit" name="admin.pages.edit" />
                                    <label for="admin.pages.edit">admin.pages.edit</label>
                                    <span class="small">: Modifier les pages</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.pages.delete" name="admin.pages.delete" />
                                    <label for="admin.pages.delete">admin.pages.delete</label>
                                    <span class="small">: Supprimer les pages</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.topics.*" name="admin.topics.*" />
                                <label for="admin.topics.*">admin.topics.*</label>
                                <span class="small">: Tout sur les affaires</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.topics.view" name="admin.topics.view" />
                                    <label for="admin.topics.view">admin.topics.view</label>
                                    <span class="small">: Voir les affaires</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.topics.create" name="admin.topics.create" />
                                    <label for="admin.topics.create">admin.topics.create</label>
                                    <span class="small">: Créer des affaires</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.topics.edit" name="admin.topics.edit" />
                                    <label for="admin.topics.edit">admin.topics.edit</label>
                                    <span class="small">: Modifier les affaires</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.topics.delete" name="admin.topics.delete" />
                                    <label for="admin.topics.delete">admin.topics.delete</label>
                                    <span class="small">: Supprimer les affaires</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.questions.*" name="admin.questions.*" />
                                <label for="admin.questions.*">admin.questions.*</label>
                                <span class="small">: Tout sur les questions</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.questions.view" name="admin.questions.view" />
                                    <label for="admin.questions.view">admin.questions.view</label>
                                    <span class="small">: Voir les questions</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.questions.create" name="admin.questions.create" />
                                    <label for="admin.questions.create">admin.questions.create</label>
                                    <span class="small">: Créer des questions</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.questions.edit" name="admin.questions.edit" />
                                    <label for="admin.questions.edit">admin.questions.edit</label>
                                    <span class="small">: Modifier les questions</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.questions.delete" name="admin.questions.delete" />
                                    <label for="admin.questions.delete">admin.questions.delete</label>
                                    <span class="small">: Supprimer les questions</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.events.*" name="admin.events.*" />
                                <label for="admin.events.*">admin.events.*</label>
                                <span class="small">: Tout sur les évènements</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.events.view" name="admin.events.view" />
                                    <label for="admin.events.view">admin.events.view</label>
                                    <span class="small">: Voir les évènements</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.events.create" name="admin.events.create" />
                                    <label for="admin.events.create">admin.events.create</label>
                                    <span class="small">: Créer des évènements</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.events.edit" name="admin.events.edit" />
                                    <label for="admin.events.edit">admin.events.edit</label>
                                    <span class="small">: Modifier les évènements</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.events.delete" name="admin.events.delete" />
                                    <label for="admin.events.delete">admin.events.delete</label>
                                    <span class="small">: Supprimer les évènements</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="ms-3">
                                <input type="checkbox" id="admin.clubs.*" name="admin.clubs.*" />
                                <label for="admin.clubs.*">admin.clubs.*</label>
                                <span class="small">: Tout sur les clubs</span>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.clubs.view" name="admin.clubs.view" />
                                    <label for="admin.clubs.view">admin.clubs.view</label>
                                    <span class="small">: Voir les clubs</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.clubs.create" name="admin.clubs.create" />
                                    <label for="admin.clubs.create">admin.clubs.create</label>
                                    <span class="small">: Créer des clubs</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.clubs.edit" name="admin.clubs.edit" />
                                    <label for="admin.clubs.edit">admin.clubs.edit</label>
                                    <span class="small">: Modifier les clubs</span>
                                </div>
                                <div class="ms-3">
                                    <input type="checkbox" id="admin.clubs.delete" name="admin.clubs.delete" />
                                    <label for="admin.clubs.delete">admin.clubs.delete</label>
                                    <span class="small">: Supprimer les clubs</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="d-flex justify-content-end mt-4">
                        <input type="submit" name="permissions" class="btn bg-gradient-primary m-0 ms-2" value="Modifier">
                    </div>
                </form>
            </div>
        </div>
        </#if>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="/js/checkboxes.js"></script>
<script>
    if (document.querySelector('.datetimepicker')) {
      flatpickr('.datetimepicker', {
        allowInput: true
      }); // flatpickr
    }
    function oneYear() {
        var date = new Date();
        if (date.getMonth() > 7) {
            date.setFullYear(date.getFullYear() + 1);
        }
        date.setMonth(7);
        date.setDate(31);
        document.getElementById("expiration").value = date.toISOString().split('T')[0];
    }
    function fiveYears() {
        var date = new Date();
        if (date.getMonth() > 7) {
            date.setFullYear(date.getFullYear() + 5);
        } else {
            date.setFullYear(date.getFullYear() + 4);
        }
        date.setMonth(7);
        date.setDate(31);
        document.getElementById("expiration").value = date.toISOString().split('T')[0];
    }
    <#list user.permissions as permission>
    checkForId("${permission}");
    </#list>
</script>
</@template.page>
