package com.artemchep.keyguard.feature.home.vault.add

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import arrow.core.flatten
import arrow.core.partially1
import arrow.core.partially2
import arrow.core.widen
import arrow.optics.Optional
import com.artemchep.keyguard.common.io.attempt
import com.artemchep.keyguard.common.io.bind
import com.artemchep.keyguard.common.io.effectTap
import com.artemchep.keyguard.common.io.ioEffect
import com.artemchep.keyguard.common.io.launchIn
import com.artemchep.keyguard.common.io.toIO
import com.artemchep.keyguard.common.model.DSecret
import com.artemchep.keyguard.common.model.Loadable
import com.artemchep.keyguard.common.model.ToastMessage
import com.artemchep.keyguard.common.model.TotpToken
import com.artemchep.keyguard.common.model.UsernameVariation2
import com.artemchep.keyguard.common.model.create.CreateRequest
import com.artemchep.keyguard.common.model.create.address1
import com.artemchep.keyguard.common.model.create.address2
import com.artemchep.keyguard.common.model.create.address3
import com.artemchep.keyguard.common.model.create.brand
import com.artemchep.keyguard.common.model.create.card
import com.artemchep.keyguard.common.model.create.cardholderName
import com.artemchep.keyguard.common.model.create.city
import com.artemchep.keyguard.common.model.create.code
import com.artemchep.keyguard.common.model.create.company
import com.artemchep.keyguard.common.model.create.country
import com.artemchep.keyguard.common.model.create.email
import com.artemchep.keyguard.common.model.create.expMonth
import com.artemchep.keyguard.common.model.create.expYear
import com.artemchep.keyguard.common.model.create.firstName
import com.artemchep.keyguard.common.model.create.fromMonth
import com.artemchep.keyguard.common.model.create.fromYear
import com.artemchep.keyguard.common.model.create.identity
import com.artemchep.keyguard.common.model.create.lastName
import com.artemchep.keyguard.common.model.create.licenseNumber
import com.artemchep.keyguard.common.model.create.middleName
import com.artemchep.keyguard.common.model.create.note
import com.artemchep.keyguard.common.model.create.number
import com.artemchep.keyguard.common.model.create.passportNumber
import com.artemchep.keyguard.common.model.create.phone
import com.artemchep.keyguard.common.model.create.postalCode
import com.artemchep.keyguard.common.model.create.ssn
import com.artemchep.keyguard.common.model.create.state
import com.artemchep.keyguard.common.model.create.title
import com.artemchep.keyguard.common.model.create.username
import com.artemchep.keyguard.common.model.creditCards
import com.artemchep.keyguard.common.model.fileName
import com.artemchep.keyguard.common.model.fileSize
import com.artemchep.keyguard.common.model.title
import com.artemchep.keyguard.common.model.titleH
import com.artemchep.keyguard.common.service.clipboard.ClipboardService
import com.artemchep.keyguard.common.service.logging.LogRepository
import com.artemchep.keyguard.common.usecase.AddCipher
import com.artemchep.keyguard.common.usecase.CipherUnsecureUrlCheck
import com.artemchep.keyguard.common.usecase.CopyText
import com.artemchep.keyguard.common.usecase.GetAccounts
import com.artemchep.keyguard.common.usecase.GetCiphers
import com.artemchep.keyguard.common.usecase.GetCollections
import com.artemchep.keyguard.common.usecase.GetFolders
import com.artemchep.keyguard.common.usecase.GetGravatarUrl
import com.artemchep.keyguard.common.usecase.GetMarkdown
import com.artemchep.keyguard.common.usecase.GetOrganizations
import com.artemchep.keyguard.common.usecase.GetProfiles
import com.artemchep.keyguard.common.usecase.GetTotpCode
import com.artemchep.keyguard.common.usecase.ShowMessage
import com.artemchep.keyguard.common.util.flow.EventFlow
import com.artemchep.keyguard.common.util.flow.combineToList
import com.artemchep.keyguard.common.util.flow.foldAsList
import com.artemchep.keyguard.common.util.flow.persistingStateIn
import com.artemchep.keyguard.common.util.validLuhn
import com.artemchep.keyguard.feature.apppicker.AppPickerResult
import com.artemchep.keyguard.feature.apppicker.AppPickerRoute
import com.artemchep.keyguard.feature.auth.common.SwitchFieldModel
import com.artemchep.keyguard.feature.auth.common.TextFieldModel2
import com.artemchep.keyguard.feature.auth.common.util.ValidationUri
import com.artemchep.keyguard.feature.auth.common.util.format
import com.artemchep.keyguard.feature.auth.common.util.validateUri
import com.artemchep.keyguard.feature.auth.common.util.validatedTitle
import com.artemchep.keyguard.feature.confirmation.ConfirmationRoute
import com.artemchep.keyguard.feature.confirmation.createConfirmationDialogIntent
import com.artemchep.keyguard.feature.confirmation.organization.FolderInfo
import com.artemchep.keyguard.feature.confirmation.organization.OrganizationConfirmationResult
import com.artemchep.keyguard.feature.confirmation.organization.OrganizationConfirmationRoute
import com.artemchep.keyguard.feature.datepicker.DatePickerResult
import com.artemchep.keyguard.feature.datepicker.DatePickerRoute
import com.artemchep.keyguard.feature.filepicker.FilePickerIntent
import com.artemchep.keyguard.feature.filepicker.humanReadableByteCountSI
import com.artemchep.keyguard.feature.home.vault.add.attachment.SkeletonAttachment
import com.artemchep.keyguard.feature.home.vault.add.attachment.SkeletonAttachmentItemFactory
import com.artemchep.keyguard.feature.home.vault.component.obscurePassword
import com.artemchep.keyguard.feature.home.vault.screen.VaultViewRoute
import com.artemchep.keyguard.feature.navigation.NavigationIntent
import com.artemchep.keyguard.feature.navigation.registerRouteResultReceiver
import com.artemchep.keyguard.feature.navigation.state.PersistedStorage
import com.artemchep.keyguard.feature.navigation.state.RememberStateFlowScope
import com.artemchep.keyguard.feature.navigation.state.TranslatorScope
import com.artemchep.keyguard.feature.navigation.state.copy
import com.artemchep.keyguard.feature.navigation.state.produceScreenState
import com.artemchep.keyguard.platform.leParseUri
import com.artemchep.keyguard.platform.parcelize.LeParcelable
import com.artemchep.keyguard.platform.parcelize.LeParcelize
import com.artemchep.keyguard.provider.bitwarden.usecase.autofill
import com.artemchep.keyguard.res.Res
import com.artemchep.keyguard.ui.FlatItemAction
import com.artemchep.keyguard.ui.icons.ChevronIcon
import com.artemchep.keyguard.ui.icons.IconBox
import com.artemchep.keyguard.ui.icons.Stub
import com.artemchep.keyguard.ui.icons.icon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import org.kodein.di.compose.localDI
import org.kodein.di.direct
import org.kodein.di.instance
import java.io.Serializable
import java.util.UUID

// TODO: Support hide password option
@Composable
fun produceAddScreenState(
    args: AddRoute.Args,
) = with(localDI().direct) {
    produceAddScreenState(
        args = args,
        getAccounts = instance(),
        getProfiles = instance(),
        getOrganizations = instance(),
        getCollections = instance(),
        getFolders = instance(),
        getCiphers = instance(),
        getTotpCode = instance(),
        getGravatarUrl = instance(),
        getMarkdown = instance(),
        logRepository = instance(),
        clipboardService = instance(),
        cipherUnsecureUrlCheck = instance(),
        showMessage = instance(),
        addCipher = instance(),
    )
}

@kotlinx.serialization.Serializable
@LeParcelize
data class AddItemOwnershipData(
    val accountId: String?,
    val folder: FolderInfo,
    val organizationId: String?,
    val collectionIds: Set<String>,
) : LeParcelable

@Composable
fun produceAddScreenState(
    args: AddRoute.Args,
    getAccounts: GetAccounts,
    getProfiles: GetProfiles,
    getOrganizations: GetOrganizations,
    getCollections: GetCollections,
    getFolders: GetFolders,
    getCiphers: GetCiphers,
    getTotpCode: GetTotpCode,
    getGravatarUrl: GetGravatarUrl,
    getMarkdown: GetMarkdown,
    logRepository: LogRepository,
    clipboardService: ClipboardService,
    cipherUnsecureUrlCheck: CipherUnsecureUrlCheck,
    showMessage: ShowMessage,
    addCipher: AddCipher,
): Loadable<AddState> = produceScreenState(
    key = "cipher_add",
    initial = Loadable.Loading,
    args = arrayOf(
        args,
        getAccounts,
        getOrganizations,
        getCollections,
        getFolders,
        getCiphers,
        getTotpCode,
    ),
) {
    val copyText = copy(clipboardService)
    val markdown = getMarkdown().first()

    val ownershipFlow = produceOwnershipFlow(
        args = args,
        getProfiles = getProfiles,
        getOrganizations = getOrganizations,
        getCollections = getCollections,
        getFolders = getFolders,
        getCiphers = getCiphers,
    )

    val loginHolder = produceLoginState(
        args = args,
        ownershipFlow = ownershipFlow,
        copyText = copyText,
        getTotpCode = getTotpCode,
        getGravatarUrl = getGravatarUrl,
    )
    val cardHolder = produceCardState(
        args = args,
    )
    val identityHolder = produceIdentityState(
        args = args,
    )
    val noteHolder = produceNoteState(
        args = args,
        markdown = markdown,
    )

    val typeFlow = kotlin.run {
        val initialValue = args.type
            ?: args.initialValue?.type
            // this should never happen
            ?: DSecret.Type.None
        flowOf(initialValue)
    }

    val filePickerIntentSink = EventFlow<FilePickerIntent<*>>()

    val sideEffects = AddState.SideEffects(
        filePickerIntentFlow = filePickerIntentSink,
    )

    val passkeysFactories = kotlin.run {
        val passkeyFactory = AddStateItemPasskeyFactory(
        )
        listOf(
            passkeyFactory,
        )
    }
    val passkeysFlow = foo3(
        logRepository = logRepository,
        scope = "passkey",
        initial = kotlin.run {
            val existingPasskeys = args.initialValue?.login?.fido2Credentials
                .orEmpty()
            existingPasskeys
        },
        initialType = { "passkey" },
        factories = passkeysFactories,
        afterList = {
            if (isEmpty()) {
                return@foo3
            }

            val header = AddStateItem.Section(
                id = "passkey.section",
                text = translate(Res.strings.passkeys),
            )
            add(0, header)
        },
    )

    val urisFactories = kotlin.run {
        val uriFactory = AddStateItemUriFactory(
            cipherUnsecureUrlCheck = cipherUnsecureUrlCheck,
        )
        listOf(
            uriFactory,
        )
    }
    val urisFlow = foo3(
        logRepository = logRepository,
        scope = "uri",
        initial = kotlin.run {
            val existingUris = args.initialValue?.uris
                .orEmpty()
            existingUris.autofill(
                applicationId = args.autofill?.applicationId,
                webDomain = args.autofill?.webDomain,
                webScheme = args.autofill?.webScheme,
            )
        },
        initialType = { "uri" },
        factories = urisFactories,
        afterList = {
            val header = AddStateItem.Section(
                id = "uri.section",
                text = translate(Res.strings.uris),
            )
            add(0, header)
        },
        extra = {
            typeBasedAddItem(
                translator = this@produceScreenState,
                scope = "uri",
                typesFlow = flowOf(
                    listOf(
                        Foo2Type(
                            type = "uri",
                            name = translate(Res.strings.uri),
                        ),
                    ),
                ),
            )
        },
    )

    val attachmentsFactories = kotlin.run {
        val attachmentFactory = SkeletonAttachmentItemFactory()
        listOf(
            attachmentFactory,
        )
    }
    val attachmentsFlow = foo3(
        logRepository = logRepository,
        scope = "attachment",
        initial = args.initialValue?.attachments.orEmpty()
            .map {
                when (it) {
                    is DSecret.Attachment.Remote -> {
                        SkeletonAttachment.Remote(
                            identity = SkeletonAttachment.Remote.Identity(
                                id = it.id,
                            ),
                            name = it.fileName(),
                            size = it.fileSize()?.let(::humanReadableByteCountSI).orEmpty(),
                        )
                    }

                    is DSecret.Attachment.Local -> {
                        val uri = kotlin.runCatching {
                            leParseUri(it.url)
                        }.getOrNull()
                            ?: return@map null
                        SkeletonAttachment.Local(
                            identity = SkeletonAttachment.Local.Identity(
                                id = it.id,
                                uri = uri,
                                size = it.fileSize(),
                            ),
                            name = it.fileName(),
                            size = it.fileSize()?.let(::humanReadableByteCountSI).orEmpty(),
                        )
                    }
                }
            }
            .filterNotNull(),
        initialType = { "attachment" },
        factories = attachmentsFactories,
        afterList = {
//            val header = AddStateItem.Section(
//                id = "attachment.section",
//                text = "Attachments",
//            )
//            add(0, header)
        },
        extra = {
            val action = FlatItemAction(
                title = "Attachment",
                onClick = {
                    val intent = FilePickerIntent.OpenDocument { info ->
                        val msg = ToastMessage(
                            title = "Picked a file!",
                            text = info?.name?.toString(),
                        )
                        showMessage.copy(msg)

                        if (info != null) {
                            val model = SkeletonAttachment.Local(
                                identity = SkeletonAttachment.Local.Identity(
                                    id = UUID.randomUUID().toString(),
                                    uri = info.uri,
                                    size = info.size,
                                ),
                                name = info.name ?: "File",
                                size = info.size?.let(::humanReadableByteCountSI).orEmpty(),
                            )
                            add("attachment", model)
                        }
                    }
                    filePickerIntentSink.emit(intent)
                },
            )
            val item = AddStateItem.Add(
                id = "attachment.add",
                text = translate(Res.strings.list_add),
                actions = listOf(action),
            )
            flowOf(emptyList())
        },
    )

    val typeItemsFlow = typeFlow
        .flatMapLatest { type ->
            when (type) {
                DSecret.Type.Login ->
                    combine(
                        passkeysFlow,
                        urisFlow,
                    ) { passkeys, uris ->
                        loginHolder.items + passkeys + uris
                    }

                DSecret.Type.Card -> flowOf(cardHolder.items)
                DSecret.Type.Identity -> flowOf(identityHolder.items)
                DSecret.Type.SecureNote -> flowOf(noteHolder.items)
                DSecret.Type.None -> flowOf(emptyList())
            }
        }
    val miscItems by lazy {
        listOf(
            AddStateItem.Section(
                id = "misc",
            ),
            AddStateItem.Note(
                id = "misc.note",
                state = noteHolder.note.state,
                markdown = markdown,
            ),
        )
    }
    val miscFlow = typeFlow
        .map { type ->
            val hasNotes = when (type) {
                DSecret.Type.SecureNote -> true
                else -> false
            }
            hasNotes
        }
        .distinctUntilChanged()
        .map { hasNotes ->
            miscItems.takeUnless { hasNotes }.orEmpty()
        }

    val favouriteSink = mutablePersistedFlow("favourite") {
        args.initialValue?.favorite
            ?: false
    }
    val favouriteFlow = favouriteSink
        .map { checked ->
            SwitchFieldModel(
                checked = checked,
                onChange = favouriteSink::value::set,
            )
        }
    val reprompt =
        AddStateItem.Switch(
            id = "reprompt",
            title = translate(Res.strings.additem_auth_reprompt_title),
            text = translate(Res.strings.additem_auth_reprompt_text),
            state = LocalStateItem(
                flow = kotlin.run {
                    val sink = mutablePersistedFlow("reprompt") {
                        args.initialValue?.reprompt
                            ?: false
                    }
                    sink.map {
                        SwitchFieldModel(
                            checked = it,
                            onChange = sink::value::set,
                        )
                    }
                        .persistingStateIn(
                            scope = screenScope,
                            started = SharingStarted.WhileSubscribed(),
                            initialValue = SwitchFieldModel(),
                        )
                },
                populator = {
                    copy(
                        reprompt = it.checked,
                    )
                },
            ),
        )
    val actions = listOf(reprompt)
        .map { item ->
            when (item) {
                is AddStateItem.Switch ->
                    item
                        .state.flow
                        .map { model ->
                            FlatItemAction(
                                id = item.id,
                                title = item.title,
                                text = item.text,
                                leading = icon(Icons.Outlined.Password),
                                trailing = {
                                    Switch(
                                        checked = model.checked,
                                        onCheckedChange = model.onChange,
                                    )
                                },
                                onClick = model.onChange?.partially1(!model.checked),
                            )
                        }

                else -> {
                    TODO()
                }
            }
        }
        .foldAsList()

    val fieldsFactories = kotlin.run {
        val textFactory = AddStateItemFieldTextFactory()
        val booleanFactory = AddStateItemFieldBooleanFactory()
        val linkedIdFactory = AddStateItemFieldLinkedIdFactory(
            typeFlow = typeFlow,
        )
        listOf(
            textFactory,
            booleanFactory,
            linkedIdFactory,
        )
    }
    val fieldsFlow = foo3(
        logRepository = logRepository,
        scope = "field",
        initial = args.initialValue?.fields.orEmpty(),
        initialType = { field ->
            when (field.type) {
                DSecret.Field.Type.Text,
                DSecret.Field.Type.Hidden,
                -> "field.text"

                DSecret.Field.Type.Boolean -> "field.boolean"
                DSecret.Field.Type.Linked -> "field.linked_id"
            }
        },
        factories = fieldsFactories,
        afterList = {
            val header = AddStateItem.Section(
                id = "field.section",
                text = translate(Res.strings.custom_fields),
            )
            add(0, header)
        },
        extra = {
            typeBasedAddItem(
                translator = this@produceScreenState,
                scope = "field",
                typesFlow = typeFlow
                    .map { type ->
                        val textType = Foo2Type(
                            type = "field.text",
                            name = translate(Res.strings.field_type_text),
                        )
                        val booleanType = Foo2Type(
                            type = "field.boolean",
                            name = translate(Res.strings.field_type_boolean),
                        )
                        val linkedIdType = Foo2Type(
                            type = "field.linked_id",
                            name = translate(Res.strings.field_type_linked),
                        )
                        when (type) {
                            DSecret.Type.Login,
                            DSecret.Type.Card,
                            DSecret.Type.Identity,
                            -> listOf(
                                textType,
                                booleanType,
                                linkedIdType,
                            )

                            DSecret.Type.SecureNote -> listOf(
                                textType,
                                booleanType,
                            )

                            DSecret.Type.None -> emptyList()
                        }
                    },
            )
        },
    )

    val titleItem = AddStateItem.Title(
        id = "title",
        state = LocalStateItem(
            flow = kotlin.run {
                val key = "title"
                val sink = mutablePersistedFlow(key) {
                    args.name
                        ?: args.initialValue?.name
                        ?: args.autofill?.webDomain
                        ?: ""
                }
                val state = asComposeState<String>(key)
                combine(
                    sink
                        .validatedTitle(this),
                    typeFlow,
                ) { validatedTitle, type ->
                    TextFieldModel2.of(
                        state = state,
                        hint = translate(type.titleH()),
                        validated = validatedTitle,
                        onChange = state::value::set,
                    )
                }
                    .persistingStateIn(
                        scope = screenScope,
                        started = SharingStarted.WhileSubscribed(1000L),
                        initialValue = TextFieldModel2.empty,
                    )
            },
            populator = { field ->
                copy(
                    title = field.text,
                )
            },
        ),
    )
    val titleSuggestions = createItem2(
        prefix = "title",
        key = "title",
        args = args,
        getSuggestion = { it.name },
        selectedFlow = titleItem.state.flow.map { it.text },
        concealed = false,
        onClick = {
            titleItem.state.flow.value.onChange?.invoke(it)
        },
    )
    val items1 = listOfNotNull(
        titleItem,
        titleSuggestions,
    )

    fun stetify(flow: Flow<List<AddStateItem>>) = flow
        .map { items ->
            items
                .mapNotNull { item ->
                    val stateHolder = item as? AddStateItem.HasState<Any?>
                        ?: return@mapNotNull null

                    val state = stateHolder.state
                    val flow = state.flow
                        .map { v ->
                            state.populator
                                .partially2(v)
                        }
                    item.id to flow
                }
        }

    val title = if (args.ownershipRo) {
        translate(Res.strings.additem_header_edit_title)
    } else {
        translate(Res.strings.additem_header_new_title)
    }
    val itfff = combine(
        typeItemsFlow,
        fieldsFlow,
        attachmentsFlow,
        miscFlow,
    ) { arr ->
        arr.toList().flatten()
    }
        .onEach { l ->
            logRepository.post("Foo3", "combine ${l.size}")
        }

    val outputFlow = combine(
        stetify(itfff),
        stetify(flowOf(items1 + reprompt)),
    ) { arr ->
        arr
            .flatMap {
                it
            }
    }
        .flatMapLatest { populatorFlows ->
            val typePopulator =
                typeFlow
                    .map { type ->
                        val f = fun(r: CreateRequest): CreateRequest {
                            return r.copy(type = type)
                        }
                        f
                    }
            val favouritePopulator =
                favouriteSink
                    .map { favourite ->
                        val f = fun(r: CreateRequest): CreateRequest {
                            return r.copy(favorite = favourite)
                        }
                        f
                    }
            val ownershipPopulator =
                ownershipFlow
                    .map { ownership ->
                        val f = fun(r: CreateRequest): CreateRequest {
                            val requestOwnership = CreateRequest.Ownership2(
                                accountId = ownership.data.accountId,
                                folder = ownership.data.folderId,
                                organizationId = ownership.data.organizationId,
                                collectionIds = ownership.data.collectionIds,
                            )
                            return r.copy(ownership2 = requestOwnership)
                        }
                        f
                    }
            (populatorFlows.map { it.second } + ownershipPopulator + favouritePopulator + typePopulator)
                .combineToList()
        }
        .map { populators ->
            populators.fold(
                initial = CreateRequest(
                    now = Clock.System.now(),
                ),
            ) { y, x -> x(y) }
        }
        .distinctUntilChanged()
    val f = combine(
        actions,
        favouriteFlow,
        ownershipFlow,
        itfff,
        outputFlow,
    ) { q, s, c, x, request ->
        logRepository.post(
            "Foo3",
            "create state ${x.size} (+${items1.size}) items| ${x.joinToString { it.id }}",
        )
        val state = AddState(
            title = title,
            favourite = s,
            ownership = c,
            actions = q,
            items = items1 + x,
            sideEffects = sideEffects,
            onSave = {
                val cipherIdToRequestMap = mapOf(
                    args.initialValue?.id?.takeIf { args.ownershipRo } to request,
                )
                addCipher(cipherIdToRequestMap)
                    .effectTap {
                        val intent = kotlin.run {
                            val list = mutableListOf<NavigationIntent>()
                            list += NavigationIntent.PopById(screenId, exclusive = false)
                            if (args.behavior.launchEditedCipher) {
                                val cipherId = it.first()
                                val accountId = c.data.accountId!!
                                val route = VaultViewRoute(
                                    itemId = cipherId,
                                    accountId = accountId,
                                )
                                list += NavigationIntent.NavigateToRoute(route)
                            }
                            NavigationIntent.Composite(
                                list = list,
                            )
                        }
                        navigate(intent)
                    }
                    .launchIn(appScope)
            },
        )
        Loadable.Ok(state)
    }
    f
}

class AddStateItemAttachmentFactory : Foo2Factory<AddStateItem.Attachment, DSecret.Attachment> {
    override val type: String = "attachment"

    override fun RememberStateFlowScope.release(key: String) {
        clearPersistedFlow("$key.name")
    }

    override fun RememberStateFlowScope.add(
        key: String,
        initial: DSecret.Attachment?,
    ): AddStateItem.Attachment {
        val nameKey = "$key.name"
        val nameSink = mutablePersistedFlow(nameKey) {
            initial?.fileName().orEmpty()
        }
        val nameMutableState = asComposeState<String>(nameKey)

        val textFlow = nameSink
            .map { uri ->
                TextFieldModel2(
                    text = uri,
                    state = nameMutableState,
                    onChange = nameMutableState::value::set,
                )
            }
        val stateFlow = textFlow
            .map { text ->
                AddStateItem.Attachment.State(
                    id = "id",
                    name = text,
                    size = null, // initial?.fileSize(),
                    synced = initial != null,
                )
            }
            .persistingStateIn(
                scope = screenScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = AddStateItem.Attachment.State(
                    id = "id",
                    name = TextFieldModel2.empty,
                    size = null,
                    synced = initial != null,
                ),
            )
        return AddStateItem.Attachment(
            id = key,
            state = LocalStateItem(
                flow = stateFlow,
                populator = { state ->
                    this
                },
            ),
        )
    }
}

class AddStateItemUriFactory(
    private val cipherUnsecureUrlCheck: CipherUnsecureUrlCheck,
) : Foo2Factory<AddStateItem.Url, DSecret.Uri> {
    override val type: String = "uri"

    override fun RememberStateFlowScope.release(key: String) {
        clearPersistedFlow("$key.uri")
        clearPersistedFlow("$key.match_type")
    }

    override fun RememberStateFlowScope.add(
        key: String,
        initial: DSecret.Uri?,
    ): AddStateItem.Url {
        val uriKey = "$key.uri"
        val uriSink = mutablePersistedFlow(uriKey) {
            initial?.uri.orEmpty()
        }
        val uriMutableState = asComposeState<String>(uriKey)

        val matchTypeSink = mutablePersistedFlow("$key.match_type") {
            initial?.match ?: DSecret.Uri.MatchType.default
        }

        val actionsAppPickerItem = FlatItemAction(
            leading = icon(Icons.Outlined.Apps),
            title = translate(Res.strings.uri_match_app_title),
            trailing = {
                ChevronIcon()
            },
            onClick = {
                val route = registerRouteResultReceiver(AppPickerRoute) { result ->
                    if (result is AppPickerResult.Confirm) {
                        uriMutableState.value = result.uri
                        matchTypeSink.value = DSecret.Uri.MatchType.default
                    }
                }
                val intent = NavigationIntent.NavigateToRoute(
                    route = route,
                )
                navigate(intent)
            },
        )
        // Add a ability to change the
        // match type via an option.
        val actionsMatchTypeItemFlow = matchTypeSink
            .map { selectedMatchType ->
                val text = translate(selectedMatchType.titleH())
                FlatItemAction(
                    leading = icon(Icons.Stub),
                    title = translate(Res.strings.uri_match_detection_title),
                    text = text,
                    onClick = {
                        val items = DSecret.Uri.MatchType.entries
                            .map { type ->
                                val typeTitle = translate(type.titleH())
                                ConfirmationRoute.Args.Item.EnumItem.Item(
                                    key = type.name,
                                    title = typeTitle,
                                )
                            }
                        val intent = createConfirmationDialogIntent(
                            item = ConfirmationRoute.Args.Item.EnumItem(
                                key = "name",
                                value = selectedMatchType.name,
                                items = items,
                                docs = mapOf(
                                    DSecret.Uri.MatchType.Domain.name to ConfirmationRoute.Args.Item.EnumItem.Doc(
                                        text = translate(Res.strings.uri_match_detection_domain_note),
                                        url = "https://bitwarden.com/help/uri-match-detection/#base-domain",
                                    ),
                                    DSecret.Uri.MatchType.Host.name to ConfirmationRoute.Args.Item.EnumItem.Doc(
                                        text = translate(Res.strings.uri_match_detection_host_note),
                                        url = "https://bitwarden.com/help/uri-match-detection/#host",
                                    ),
                                    DSecret.Uri.MatchType.StartsWith.name to ConfirmationRoute.Args.Item.EnumItem.Doc(
                                        text = translate(Res.strings.uri_match_detection_startswith_note),
                                        url = "https://bitwarden.com/help/uri-match-detection/#starts-with",
                                    ),
                                    DSecret.Uri.MatchType.Exact.name to ConfirmationRoute.Args.Item.EnumItem.Doc(
                                        text = translate(Res.strings.uri_match_detection_exact_note),
                                        url = "https://bitwarden.com/help/uri-match-detection/#regular-expression",
                                    ),
                                    DSecret.Uri.MatchType.RegularExpression.name to ConfirmationRoute.Args.Item.EnumItem.Doc(
                                        text = translate(Res.strings.uri_match_detection_regex_note),
                                        url = "https://bitwarden.com/help/uri-match-detection/#regular-expression",
                                    ),
                                    DSecret.Uri.MatchType.Never.name to ConfirmationRoute.Args.Item.EnumItem.Doc(
                                        text = translate(Res.strings.uri_match_detection_never_note),
                                        url = "https://bitwarden.com/help/uri-match-detection/#exact",
                                    ),
                                ),
                            ),
                            title = translate(Res.strings.uri_match_detection_title),
                        ) { newMatchTypeKey ->
                            matchTypeSink.value =
                                DSecret.Uri.MatchType.valueOf(newMatchTypeKey)
                        }
                        navigate(intent)
                    },
                )
            }
        val actionsFlow = actionsMatchTypeItemFlow
            .map {
                listOf(it, actionsAppPickerItem)
            }

        val textFlow = uriSink
            .map { uri ->
                TextFieldModel2(
                    text = uri,
                    state = uriMutableState,
                    onChange = uriMutableState::value::set,
                )
            }
        val stateFlow = combine(
            textFlow,
            matchTypeSink,
            actionsFlow,
        ) { text, matchType, actions ->
            val badge = when (matchType) {
                DSecret.Uri.MatchType.Never,
                DSecret.Uri.MatchType.Host,
                DSecret.Uri.MatchType.Domain,
                DSecret.Uri.MatchType.Exact,
                DSecret.Uri.MatchType.StartsWith,
                -> {
                    val validationUri = validateUri(
                        uri = text.text,
                        allowBlank = true,
                    )
                    if (validationUri == ValidationUri.OK) {
                        val isUnsecure = cipherUnsecureUrlCheck(text.text)
                        if (isUnsecure) {
                            TextFieldModel2.Vl(
                                type = TextFieldModel2.Vl.Type.WARNING,
                                text = translate(Res.strings.uri_unsecure),
                            )
                        } else {
                            null
                        }
                    } else {
                        validationUri.format(this)
                            ?.let { error ->
                                TextFieldModel2.Vl(
                                    type = TextFieldModel2.Vl.Type.WARNING,
                                    text = error,
                                )
                            }
                    }
                }

                DSecret.Uri.MatchType.RegularExpression -> {
                    null
                }
            }
            AddStateItem.Url.State(
                options = actions,
                text = text.copy(vl = badge),
                matchType = matchType,
                matchTypeTitle = matchType
                    .takeIf { it != DSecret.Uri.MatchType.default }
                    ?.let {
                        val name = translate(it.titleH())
                        name
                    },
            )
        }
            .persistingStateIn(
                scope = screenScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = AddStateItem.Url.State(
                    text = TextFieldModel2.empty,
                    matchType = DSecret.Uri.MatchType.default,
                ),
            )
        return AddStateItem.Url(
            id = key,
            state = LocalStateItem(
                flow = stateFlow,
                populator = { state ->
                    val field = DSecret.Uri(
                        uri = state.text.text,
                        match = state.matchType,
                    )
                    val newUris = uris.add(field)
                    copy(uris = newUris)
                },
            ),
        )
    }
}

class AddStateItemPasskeyFactory(
) : Foo2Factory<AddStateItem.Passkey, DSecret.Login.Fido2Credentials> {
    @kotlinx.serialization.Serializable
    private data class PasskeyHolder(
        val data: PasskeyData? = null,
    )

    @kotlinx.serialization.Serializable
    private data class PasskeyData(
        val credentialId: String,
        val keyType: String, // public-key
        val keyAlgorithm: String, // ECDSA
        val keyCurve: String, // P-256
        val keyValue: String,
        val rpId: String,
        val rpName: String,
        val counter: Int?,
        val userHandle: String,
        val userName: String? = null,
        val userDisplayName: String? = null,
        val discoverable: Boolean,
        val creationDate: Instant,
    )

    private fun PasskeyData.toDomainOrNull(): DSecret.Login.Fido2Credentials? {
        return DSecret.Login.Fido2Credentials(
            credentialId = credentialId,
            keyType = keyType,
            keyAlgorithm = keyAlgorithm,
            keyCurve = keyCurve,
            keyValue = keyValue,
            rpId = rpId,
            rpName = rpName,
            counter = counter,
            userHandle = userHandle,
            userName = userName,
            userDisplayName = userDisplayName,
            discoverable = discoverable,
            creationDate = creationDate,
        )
    }

    override val type: String = "passkey"

    override fun RememberStateFlowScope.release(key: String) {
        clearPersistedFlow("$key.data")
    }

    override fun RememberStateFlowScope.add(
        key: String,
        initial: DSecret.Login.Fido2Credentials?,
    ): AddStateItem.Passkey {
        val dataKey = "$key.data"
        val dataSink = mutablePersistedFlow(
            dataKey,
            serialize = { json, m: PasskeyHolder ->
                json.encodeToString(m)
            },
            deserialize = { json, m: String ->
                json.decodeFromString(m)
            },
        ) {
            val data = initial?.let {
                PasskeyData(
                    credentialId = it.credentialId,
                    keyType = it.keyType,
                    keyAlgorithm = it.keyAlgorithm,
                    keyCurve = it.keyCurve,
                    keyValue = it.keyValue,
                    rpId = it.rpId,
                    rpName = it.rpName,
                    counter = it.counter,
                    userHandle = it.userHandle,
                    userName = it.userName,
                    userDisplayName = it.userDisplayName,
                    discoverable = it.discoverable,
                    creationDate = it.creationDate,
                )
            }
            PasskeyHolder(data = data)
        }

        val stateFlow = dataSink
            .map { holder ->
                val passkey = holder.data?.toDomainOrNull()
                AddStateItem.Passkey.State(
                    passkey = passkey,
                )
            }
            .persistingStateIn(
                scope = screenScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = AddStateItem.Passkey.State(
                    passkey = null,
                ),
            )
        return AddStateItem.Passkey(
            id = key,
            state = LocalStateItem(
                flow = stateFlow,
                populator = { state ->
                    val passkey = state.passkey
                    // Do not modify the state if the
                    // passkey does not exist.
                        ?: return@LocalStateItem this
                    val newFido2Credentials = fido2Credentials.add(passkey)
                    copy(fido2Credentials = newFido2Credentials)
                },
            ),
        )
    }
}

abstract class AddStateItemFieldFactory : Foo2Factory<AddStateItem.Field, DSecret.Field> {
    fun foo(
        key: String,
        flow: StateFlow<AddStateItem.Field.State>,
    ) = AddStateItem.Field(
        id = key,
        state = LocalStateItem(
            flow = flow,
            populator = { state ->
                val field = when (state) {
                    is AddStateItem.Field.State.Text -> {
                        val fieldType = if (state.hidden) {
                            DSecret.Field.Type.Hidden
                        } else {
                            DSecret.Field.Type.Text
                        }
                        DSecret.Field(
                            name = state.label.text,
                            value = state.text.text,
                            type = fieldType,
                        )
                    }

                    is AddStateItem.Field.State.Switch -> {
                        DSecret.Field(
                            name = state.label.text,
                            value = state.checked.toString(),
                            type = DSecret.Field.Type.Boolean,
                        )
                    }

                    is AddStateItem.Field.State.LinkedId -> {
                        if (state.value == null) {
                            return@LocalStateItem this
                        }

                        DSecret.Field(
                            name = state.label.text,
                            linkedId = state.value,
                            type = DSecret.Field.Type.Linked,
                        )
                    }
                }
                val newFields = fields.add(field)
                copy(fields = newFields)
            },
        ),
    )
}

class AddStateItemFieldTextFactory : AddStateItemFieldFactory() {
    override val type: String = "field.text"

    override fun RememberStateFlowScope.release(key: String) {
        clearPersistedFlow("$key.label")
        clearPersistedFlow("$key.text")
        clearPersistedFlow("$key.conceal")
    }

    override fun RememberStateFlowScope.add(
        key: String,
        initial: DSecret.Field?,
    ): AddStateItem.Field {
        val labelSink = mutablePersistedFlow("$key.label") {
            initial?.name.orEmpty()
        }
        val labelMutableState = mutableComposeState(labelSink)
        val labelFlow = labelSink.map { label ->
            TextFieldModel2(
                text = label,
                hint = "Label",
                state = labelMutableState,
                onChange = labelMutableState::value::set,
            )
        }

        val textSink = mutablePersistedFlow("$key.text") {
            initial?.value.orEmpty()
        }
        val textMutableState = mutableComposeState(textSink)
        val textFlow = textSink.map { text ->
            TextFieldModel2(
                text = text,
                hint = "Value",
                state = textMutableState,
                onChange = textMutableState::value::set,
            )
        }

        val concealSink = mutablePersistedFlow("$key.conceal") {
            initial?.type == DSecret.Field.Type.Hidden
        }

        val actionsConcealItemFlow = concealSink
            .map { conceal ->
                FlatItemAction(
                    leading = {
                        val imageVector = if (conceal) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        }
                        Crossfade(targetState = imageVector) { iv ->
                            IconBox(
                                main = iv,
                            )
                        }
                    },
                    title = "Conceal value",
                    trailing = {
                        Checkbox(
                            checked = conceal,
                            onCheckedChange = null,
                        )
                    },
                    onClick = {
                        concealSink.value = !conceal
                    },
                )
            }
        val actionsFlow = actionsConcealItemFlow
            .map { concealItem ->
                listOf(
                    concealItem,
                )
            }

        val stateFlow = combine(
            labelFlow,
            textFlow,
            concealSink,
            actionsFlow,
        ) { labelField, textField, hidden, actions ->
            AddStateItem.Field.State.Text(
                label = labelField,
                text = textField,
                hidden = hidden,
                options = actions,
            )
        }
            .persistingStateIn(
                scope = screenScope,
                started = SharingStarted.WhileSubscribed(1000L),
                initialValue = AddStateItem.Field.State.Text(
                    label = TextFieldModel2.empty,
                    text = TextFieldModel2.empty,
                ),
            )
        return foo(
            key = key,
            flow = stateFlow,
        )
    }
}

class AddStateItemFieldBooleanFactory : AddStateItemFieldFactory() {
    override val type: String = "field.boolean"

    override fun RememberStateFlowScope.release(key: String) {
        clearPersistedFlow("$key.label")
        clearPersistedFlow("$key.check")
    }

    override fun RememberStateFlowScope.add(
        key: String,
        initial: DSecret.Field?,
    ): AddStateItem.Field {
        val labelSink = mutablePersistedFlow("$key.label") {
            initial?.name.orEmpty()
        }
        val labelMutableState = mutableComposeState(labelSink)
        val labelFlow = labelSink.map { label ->
            TextFieldModel2(
                text = label,
                hint = "Label",
                state = labelMutableState,
                onChange = labelMutableState::value::set,
            )
        }

        val checkedSink = mutablePersistedFlow("$key.check") {
            initial?.value.toBoolean()
        }

        val stateFlow = combine(
            labelFlow,
            checkedSink,
        ) { label, checked ->
            AddStateItem.Field.State.Switch(
                label = label,
                checked = checked,
                onCheckedChange = checkedSink::value::set,
            )
        }
            .persistingStateIn(
                scope = screenScope,
                started = SharingStarted.WhileSubscribed(1000L),
                initialValue = AddStateItem.Field.State.Switch(
                    label = TextFieldModel2.empty,
                ),
            )
        return foo(
            key = key,
            flow = stateFlow,
        )
    }
}

class AddStateItemFieldLinkedIdFactory(
    private val typeFlow: Flow<DSecret.Type>,
) : AddStateItemFieldFactory() {
    override val type: String = "field.linked_id"

    override fun RememberStateFlowScope.release(key: String) {
        clearPersistedFlow("$key.label")
        clearPersistedFlow("$key.linked_id")
    }

    override fun RememberStateFlowScope.add(
        key: String,
        initial: DSecret.Field?,
    ): AddStateItem.Field {
        val labelSink = mutablePersistedFlow("$key.label") {
            initial?.name.orEmpty()
        }
        val labelMutableState = mutableComposeState(labelSink)
        val labelFlow = labelSink.map { label ->
            TextFieldModel2(
                text = label,
                hint = "Label",
                state = labelMutableState,
                onChange = labelMutableState::value::set,
            )
        }

        val valueSink = mutablePersistedFlow("$key.linked_id") {
            initial?.linkedId
        }
        val valueFlow = combine(
            valueSink,
            typeFlow,
        ) { linkedIdOrNull, type ->
            linkedIdOrNull
                ?.takeIf { it.type == type }
                ?: kotlin.run {
                    DSecret.Field.LinkedId.entries
                        .firstOrNull { it.type == type }
                }
        }
        val actionsAll = DSecret.Field.LinkedId.entries
            .map { actionLinkedId ->
                actionLinkedId to FlatItemAction(
                    id = actionLinkedId.name,
                    leading = {
                        val selectedLinkedId by valueFlow.collectAsState(initial = null)
                        RadioButton(
                            selected = selectedLinkedId == actionLinkedId,
                            onClick = null,
                        )
                    },
                    title = translate(actionLinkedId.titleH()),
                    onClick = {
                        valueSink.value = actionLinkedId
                    },
                )
            }
        val actionsFlow = typeFlow
            .map { type ->
                actionsAll
                    .mapNotNull { (itemLinkedId, itemAction) ->
                        itemAction
                            .takeIf { itemLinkedId.type == type }
                    }
            }
        val stateFlow = combine(
            labelFlow,
            valueFlow,
            actionsFlow,
        ) { label, linkedId, actions ->
            AddStateItem.Field.State.LinkedId(
                label = label,
                value = linkedId,
                actions = actions,
            )
        }
            .persistingStateIn(
                scope = screenScope,
                started = SharingStarted.WhileSubscribed(1000L),
                initialValue = AddStateItem.Field.State.LinkedId(
                    label = TextFieldModel2.empty,
                    value = null,
                    actions = emptyList(),
                ),
            )
        return foo(
            key = key,
            flow = stateFlow,
        )
    }
}

interface Foo2Factory<T, Default> where T : AddStateItem, T : AddStateItem.HasOptions<T> {
    val type: String

    fun RememberStateFlowScope.add(key: String, initial: Default?): T

    fun RememberStateFlowScope.release(key: String)
}

data class Foo2Type(
    val type: String,
    val name: String,
)

data class Foo2Persistable(
    val key: String,
    val type: String,
) : Serializable

data class Foo2InitialState<Argument>(
    val items: List<Item<Argument>>,
) {
    data class Item<Argument>(
        val key: String,
        val type: String,
        val argument: Argument? = null,
    )
}

fun <T, Argument> RememberStateFlowScope.foo3(
    logRepository: LogRepository,
    scope: String,
    initial: List<Argument>,
    initialType: (Argument) -> String,
    factories: List<Foo2Factory<T, Argument>>,
    afterList: MutableList<AddStateItem>.() -> Unit,
    extra: FieldBakeryScope<Argument>.() -> Flow<List<AddStateItem>> = {
        flowOf(emptyList())
    },
): Flow<List<AddStateItem>> where T : AddStateItem, T : AddStateItem.HasOptions<T> {
    val initialState = Foo2InitialState(
        items = initial
            .associateBy {
                UUID.randomUUID().toString()
            }
            .map { entry ->
                Foo2InitialState.Item(
                    key = entry.key,
                    type = initialType(entry.value),
                    argument = entry.value,
                )
            },
    )
    logRepository.post("Foo3", "Scope '$scope' with initial ${initial.size} items.")
    return foo<T, Argument>(
        scope = scope,
        initialState = initialState,
        entryAdd = { (key, type), arg ->
            val factory = factories.first { it.type == type }
            with(factory) {
                add(key, arg)
            }
        },
        entryRelease = { (key, type) ->
            val factory = factories.first { it.type == type }
            with(factory) {
                release(key)
            }
        },
        afterList = afterList,
        extra = extra,
    )
        .onEach { l ->
            logRepository.post("Foo3", "Scope '$scope' emit ${l.size}")
        }
}

fun <T, Argument> RememberStateFlowScope.foo2(
    scope: String,
    initialState: Foo2InitialState<Argument>,
    factories: List<Foo2Factory<T, Argument>>,
    afterList: MutableList<AddStateItem>.() -> Unit,
    extra: FieldBakeryScope<Argument>.() -> Flow<List<AddStateItem>> = {
        flowOf(emptyList())
    },
): Flow<List<AddStateItem>> where T : AddStateItem, T : AddStateItem.HasOptions<T> {
    return foo<T, Argument>(
        scope = scope,
        initialState = initialState,
        entryAdd = { (key, type), arg ->
            val factory = factories.first { it.type == type }
            with(factory) {
                add(key, arg)
            }
        },
        entryRelease = { (key, type) ->
            val factory = factories.first { it.type == type }
            with(factory) {
                release(key)
            }
        },
        afterList = afterList,
        extra = extra,
    )
}

interface FieldBakeryScope<Argument> {
    fun moveUp(key: String)

    fun moveDown(key: String)

    fun delete(key: String)

    fun add(type: String, arg: Argument?)
}

private fun <Argument> FieldBakeryScope<Argument>.typeBasedAddItem(
    translator: TranslatorScope,
    scope: String,
    typesFlow: Flow<List<Foo2Type>>,
): Flow<List<AddStateItem>> = typesFlow
    .map { types ->
        if (types.isEmpty()) {
            return@map null // hide add item
        }

        val actions = types
            .map { type ->
                FlatItemAction(
                    title = type.name,
                    onClick = ::add
                        .partially1(type.type)
                        .partially1(null),
                )
            }
        AddStateItem.Add(
            id = "$scope.add",
            text = translator.translate(Res.strings.list_add),
            actions = actions,
        )
    }
    .map { listOfNotNull(it) }

fun <T, Argument> RememberStateFlowScope.foo(
    // scope name,
    scope: String,
    initialState: Foo2InitialState<Argument>,
    entryAdd: RememberStateFlowScope.(Foo2Persistable, Argument?) -> T,
    entryRelease: RememberStateFlowScope.(Foo2Persistable) -> Unit,
    afterList: MutableList<AddStateItem>.() -> Unit,
    extra: FieldBakeryScope<Argument>.() -> Flow<List<AddStateItem>> = {
        flowOf(emptyList())
    },
): Flow<List<AddStateItem>> where T : AddStateItem, T : AddStateItem.HasOptions<T> {
    var inMemoryArguments = initialState
        .items
        .mapNotNull {
            if (it.argument != null) {
                it.key to it.argument
            } else {
                null
            }
        }
        .toMap()
        .toPersistentMap()

    val persistableSink = mutablePersistedFlow("$scope.keys") {
        initialState
            .items
            .map {
                Foo2Persistable(
                    key = it.key,
                    type = it.type,
                )
            }
    }

    fun move(key: String, offset: Int) = persistableSink.update { state ->
        val i = state.indexOfFirst { it.key == key }
        if (
            i == -1 ||
            i + offset < 0 ||
            i + offset >= state.size
        ) {
            return@update state
        }

        val newState = state.toMutableList()
        val item = newState.removeAt(i)
        newState.add(i + offset, item)
        newState
    }

    fun moveUp(key: String) = move(key, offset = -1)

    fun moveDown(key: String) = move(key, offset = 1)

    fun delete(key: String) = persistableSink.update { state ->
        inMemoryArguments = inMemoryArguments.remove(key)

        val i = state.indexOfFirst { it.key == key }
        if (i == -1) return@update state

        val newState = state.toMutableList()
        newState.removeAt(i)
        newState
    }

    fun add(type: String, arg: Argument?) {
        val key = "$scope.items." + UUID.randomUUID().toString()
        // Remember the argument, so we can grab it and construct something
        // persistent from it.
        if (arg != null) {
            inMemoryArguments = inMemoryArguments.put(key, arg)
        }

        val newEntry = Foo2Persistable(
            key = key,
            type = type,
        )
        persistableSink.update { state ->
            state + newEntry
        }
    }

    val scope2 = object : FieldBakeryScope<Argument> {
        override fun moveUp(key: String) = moveUp(key)

        override fun moveDown(key: String) = moveDown(key)

        override fun delete(key: String) = delete(key)

        override fun add(type: String, arg: Argument?) = add(type, arg)
    }

    val keysFlow = persistableSink
        .scan(
            initial = listOf<Foo2Persistable>(),
        ) { state, new ->
            // Find if a new entry has a different
            // type comparing to the old one.
            new.forEach { (key, type) ->
                val existingEntry = state.firstOrNull { it.key == key }
                when (val existingType = existingEntry?.type) {
                    type,
                    null,
                    -> {
                        // Do nothing.
                    }

                    else -> {
                        val item = Foo2Persistable(key, existingType)
                        entryRelease(item)
                    }
                }
            }
            // Clean-up removed entries.
            state.forEach { (key, type) ->
                val removed = new.none { it.key == key }
                if (removed) {
                    val item = Foo2Persistable(key, type)
                    entryRelease(item)
                }
            }

            new
        }
    val itemsFlow = keysFlow
        .map { state ->
            state.mapIndexed { index, entry ->
                val arg = inMemoryArguments[entry.key]
                val item = entryAdd(entry, arg)

                // Appends list-specific options to be able to reorder
                // the list or remove an item.
                val options = item.options.toMutableList()
                if (index > 0) {
                    options += FlatItemAction(
                        icon = Icons.Outlined.ArrowUpward,
                        title = translate(Res.strings.list_move_up),
                        onClick = ::moveUp.partially1(entry.key),
                    )
                }
                if (index < state.size - 1) {
                    options += FlatItemAction(
                        icon = Icons.Outlined.ArrowDownward,
                        title = translate(Res.strings.list_move_down),
                        onClick = ::moveDown.partially1(entry.key),
                    )
                }
                options += FlatItemAction(
                    icon = Icons.Outlined.DeleteForever,
                    title = translate(Res.strings.list_remove),
                    onClick = {
                        val intent = createConfirmationDialogIntent(
                            icon = icon(Icons.Outlined.DeleteForever),
                            title = translate(Res.strings.list_remove_confirmation_title),
                        ) {
                            delete(entry.key)
                        }
                        navigate(intent)
                    },
                )

                item.withOptions(options)
            }
        }
    val extraFlow = extra(scope2)
    return combine(
        itemsFlow,
        extraFlow,
    ) { items, customItems ->
        val out = items
            .widen<AddStateItem, T>()
            .toMutableList()
        out += customItems
        out.apply(afterList)
    }
        .shareIn(screenScope, SharingStarted.WhileSubscribed(5000L), replay = 1)
}

private suspend fun RememberStateFlowScope.produceOwnershipFlow(
    args: AddRoute.Args,
    getProfiles: GetProfiles,
    getOrganizations: GetOrganizations,
    getCollections: GetCollections,
    getFolders: GetFolders,
    getCiphers: GetCiphers,
): Flow<AddState.Ownership> {
    val ro = args.ownershipRo

    data class Fool<T>(
        val value: T,
        val element: AddState.SaveToElement?,
    )

    val disk = loadDiskHandle("new_item")
    val accountIdSink = mutablePersistedFlow<String?>(
        key = "ownership.account_id",
        storage = PersistedStorage.InDisk(disk),
    ) { null }

    val initialState = kotlin.run {
        if (args.initialValue != null) {
            return@run AddItemOwnershipData(
                accountId = args.initialValue.accountId,
                organizationId = args.initialValue.organizationId,
                collectionIds = args.initialValue.collectionIds,
                folder = kotlin.run {
                    val folderId = args.initialValue.folderId
                    if (folderId != null) {
                        FolderInfo.Id(folderId)
                    } else {
                        FolderInfo.None
                    }
                },
            )
        }

        // Make an account that has the most ciphers a
        // default account.
        val accountId = ioEffect {
                val accountIds = getProfiles().toIO().bind()
                    .map { it.accountId() }
                    .toSet()

                fun String.takeIfAccountIdExists() = this
                    .takeIf { id ->
                        id in accountIds
                    }

                val lastAccountId = accountIdSink.value?.takeIfAccountIdExists()
                if (lastAccountId != null) {
                    return@ioEffect lastAccountId
                }

                val ciphers = getCiphers().toIO().bind()
                ciphers
                    .asSequence()
                    .filter { it.organizationId != null }
                    .groupBy { it.accountId }
                    // the one that has the most ciphers
                    .maxByOrNull { entry -> entry.value.size }
                    // account id
                    ?.key
                    ?.takeIfAccountIdExists()
            }.attempt().bind().getOrNull()

        AddItemOwnershipData(
            accountId = accountId,
            organizationId = null,
            collectionIds = emptySet(),
            folder = FolderInfo.None,
        )
    }
    val sink = mutablePersistedFlow("ownership") {
        initialState
    }

    // If we are creating a new item, then remember the
    // last selected account to pre-select it next time.
    // TODO: Remember only when an item is created.
    sink
        .map { it.accountId }
        .onEach(accountIdSink::value::set)
        .launchIn(screenScope)

    val accountFlow = combine(
        sink
            .map { it.accountId }
            .distinctUntilChanged(),
        getProfiles(),
    ) { accountId, profiles ->
        if (accountId == null) {
            val item = AddState.SaveToElement.Item(
                key = "account.empty",
                title = translate(Res.strings.account_none),
            )
            val el = AddState.SaveToElement(
                readOnly = ro,
                items = listOf(item),
            )
            return@combine Fool(
                value = null,
                element = el,
            )
        }
        val profileOrNull = profiles
            .firstOrNull { it.accountId() == accountId }
        val el = AddState.SaveToElement(
            readOnly = ro,
            items = listOfNotNull(profileOrNull)
                .map { account ->
                    val key = "account.${account.accountId()}"
                    AddState.SaveToElement.Item(
                        key = key,
                        title = account.email,
                        text = account.accountHost,
                        accentColors = account.accentColor,
                    )
                },
        )
        Fool(
            value = accountId,
            element = el,
        )
    }

    val organizationFlow = combine(
        sink
            .map { it.organizationId }
            .distinctUntilChanged(),
        getOrganizations(),
    ) { organizationId, organizations ->
        if (organizationId == null) {
            val item = AddState.SaveToElement.Item(
                key = "organization.empty",
                title = translate(Res.strings.organization_none),
            )
            val el = AddState.SaveToElement(
                readOnly = ro,
                items = listOf(item),
            )
            return@combine Fool(
                value = null,
                element = el,
            )
        }
        val organizationOrNull = organizations
            .firstOrNull { it.id == organizationId }
        val el = AddState.SaveToElement(
            readOnly = ro,
            items = listOfNotNull(organizationOrNull)
                .map { organization ->
                    val key = "organization.${organization.id}"
                    AddState.SaveToElement.Item(
                        key = key,
                        title = organization.name,
                    )
                },
        )
        Fool(
            value = organizationId,
            element = el,
        )
    }

    val collectionFlow = combine(
        sink
            .map { it.collectionIds }
            .distinctUntilChanged(),
        getCollections(),
    ) { collectionIds, collections ->
        if (collectionIds.isEmpty()) {
            return@combine Fool(
                value = emptySet(),
                element = null,
            )
        }

        val selectedCollections = collections
            .sortedBy { it.name }
            .filter { it.id in collectionIds }
        val el = AddState.SaveToElement(
            readOnly = ro,
            items = selectedCollections
                .map { collection ->
                    val key = "collection.${collection.id}"
                    AddState.SaveToElement.Item(
                        key = key,
                        title = collection.name,
                    )
                },
        )
        Fool(
            value = collectionIds,
            element = el,
        )
    }

    val folderFlow = combine(
        sink
            .map { it.folder }
            .distinctUntilChanged(),
        getFolders(),
    ) { selectedFolder, folders ->
        when (selectedFolder) {
            is FolderInfo.None -> {
                val item = AddState.SaveToElement.Item(
                    key = "folder.empty",
                    title = translate(Res.strings.folder_none),
                )
                val el = AddState.SaveToElement(
                    readOnly = false,
                    items = listOf(item),
                )
                return@combine Fool(
                    value = selectedFolder,
                    element = el,
                )
            }

            is FolderInfo.New -> {
                val item = AddState.SaveToElement.Item(
                    key = "folder.new",
                    title = selectedFolder.name,
                )
                val el = AddState.SaveToElement(
                    readOnly = false,
                    items = listOf(item),
                )
                return@combine Fool(
                    value = selectedFolder,
                    element = el,
                )
            }

            is FolderInfo.Id -> {
                val selectedFolderOrNull = folders
                    .firstOrNull { it.id == selectedFolder.id }
                val el = AddState.SaveToElement(
                    readOnly = false,
                    items = listOfNotNull(selectedFolderOrNull)
                        .map { folder ->
                            val key = "folder.${folder.id}"
                            AddState.SaveToElement.Item(
                                key = key,
                                title = folder.name,
                            )
                        },
                )
                Fool(
                    value = selectedFolder,
                    element = el,
                )
            }
        }
    }

    return combine(
        accountFlow,
        organizationFlow,
        collectionFlow,
        folderFlow,
    ) { account, organization, collection, folder ->
        val flags = if (ro) {
            OrganizationConfirmationRoute.Args.RO_ACCOUNT or
                    OrganizationConfirmationRoute.Args.RO_ORGANIZATION or
                    OrganizationConfirmationRoute.Args.RO_COLLECTION
        } else {
            0
        }
        val data = AddState.Ownership.Data(
            accountId = account.value,
            folderId = folder.value,
            organizationId = organization.value,
            collectionIds = collection.value,
        )
        AddState.Ownership(
            data = data,
            account = account.element,
            organization = organization.element.takeIf { account.value != null },
            collection = collection.element.takeIf { account.value != null },
            folder = folder.element,
            onClick = {
                val route = registerRouteResultReceiver(
                    route = OrganizationConfirmationRoute(
                        args = OrganizationConfirmationRoute.Args(
                            decor = OrganizationConfirmationRoute.Args.Decor(
                                title = "Save to",
                                icon = Icons.Outlined.AccountBox,
                            ),
                            flags = flags,
                            accountId = account.value,
                            organizationId = organization.value,
                            folderId = folder.value,
                            collectionsIds = collection.value,
                        ),
                    ),
                ) { result ->
                    if (result is OrganizationConfirmationResult.Confirm) {
                        val newState = AddItemOwnershipData(
                            accountId = result.accountId,
                            organizationId = result.organizationId,
                            collectionIds = result.collectionsIds,
                            folder = result.folderId,
                        )
                        sink.value = newState
                    }
                }
                val intent = NavigationIntent.NavigateToRoute(route)
                navigate(intent)
            },
        )
    }
}

data class TmpLogin(
    val username: AddStateItem.Username,
    val password: AddStateItem.Password,
    val totp: AddStateItem.Totp,
    val items: List<AddStateItem>,
)

data class TmpCard(
    val cardholderName: AddStateItem.Text,
    val brand: AddStateItem.Text,
    val number: AddStateItem.Text,
    val fromDate: AddStateItem.DateMonthYear,
    val expDate: AddStateItem.DateMonthYear,
    val code: AddStateItem.Text,
    val items: List<AddStateItem>,
)

data class TmpIdentity(
    val title: AddStateItem.Text,
    val firstName: AddStateItem.Text,
    val middleName: AddStateItem.Text,
    val lastName: AddStateItem.Text,
    val address1: AddStateItem.Text,
    val address2: AddStateItem.Text,
    val address3: AddStateItem.Text,
    val city: AddStateItem.Text,
    val state: AddStateItem.Text,
    val postalCode: AddStateItem.Text,
    val country: AddStateItem.Text,
    val company: AddStateItem.Text,
    val email: AddStateItem.Text,
    val phone: AddStateItem.Text,
    val ssn: AddStateItem.Text,
    val username: AddStateItem.Text,
    val passportNumber: AddStateItem.Text,
    val licenseNumber: AddStateItem.Text,
    val items: List<AddStateItem>,
)

data class TmpNote(
    val note: AddStateItem.Note,
    val items: List<AddStateItem>,
)

private suspend fun RememberStateFlowScope.produceLoginState(
    args: AddRoute.Args,
    ownershipFlow: Flow<AddState.Ownership>,
    copyText: CopyText,
    getTotpCode: GetTotpCode,
    getGravatarUrl: GetGravatarUrl,
): TmpLogin {
    val prefix = "login"

    suspend fun <Item> createItemUsername(
        key: String,
        initialValue: String? = null,
        populator: CreateRequest.(AddStateItem.Username.State) -> CreateRequest,
        factory: (String, LocalStateItem<AddStateItem.Username.State>) -> Item,
    ) = kotlin.run {
        val id = "$prefix.$key"

        val sink = mutablePersistedFlow(id) { initialValue.orEmpty() }
        val state = mutableComposeState(sink)
        factory(
            id,
            LocalStateItem(
                flow = ownershipFlow
                    .map {
                        it.account
                            ?.items
                            ?.asSequence()
                            ?.map { it.title }
                            ?.toPersistentList()
                            ?: persistentListOf()
                    }
                    .combine(sink) { autocompleteOptions, value ->
                        val model = TextFieldModel2(
                            state = state,
                            text = value,
                            autocompleteOptions = autocompleteOptions,
                            onChange = state::value::set,
                        )
                        AddStateItem.Username.State(
                            value = model,
                            type = UsernameVariation2.of(getGravatarUrl, value),
                        )
                    }
                    .persistingStateIn(
                        scope = screenScope,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue =
                        AddStateItem.Username.State(
                            value = TextFieldModel2.empty,
                            type = UsernameVariation2.default,
                        ),
                    ),
                populator = populator,
            ),
        )
    }

    suspend fun <Item> createItem(
        key: String,
        initialValue: String? = null,
        populator: CreateRequest.(TextFieldModel2) -> CreateRequest,
        factory: (String, LocalStateItem<TextFieldModel2>) -> Item,
    ) = kotlin.run {
        val id = "$prefix.$key"

        val sink = mutablePersistedFlow(id) { initialValue.orEmpty() }
        val state = mutableComposeState(sink)
        factory(
            id,
            LocalStateItem(
                flow = sink
                    .map { value ->
                        TextFieldModel2(
                            state = state,
                            text = value,
                            onChange = state::value::set,
                        )
                    }
                    .persistingStateIn(
                        scope = screenScope,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = TextFieldModel2.empty,
                    ),
                populator = populator,
            ),
        )
    }

    val login = args.initialValue?.login

    val username = createItemUsername(
        key = "username",
        initialValue = args.username ?: args.autofill?.username ?: login?.username,
        populator = {
            copy(
                login = this.login.copy(
                    username = it.value.text,
                ),
            )
        },
    ) { id, state ->
        AddStateItem.Username(
            id = id,
            state = state,
        )
    }
    val usernameSuggestions = createItem2(
        prefix = prefix,
        key = "username",
        args = args,
        getSuggestion = { it.login?.username },
        selectedFlow = username.state.flow.map { it.value.text },
        onClick = {
            username.state.flow.value.value.onChange?.invoke(it)
        },
    )
    val password = createItem(
        key = "password",
        initialValue = args.password ?: args.autofill?.password ?: login?.password,
        populator = {
            copy(
                login = this.login.copy(
                    password = it.text,
                ),
            )
        },
    ) { id, state ->
        AddStateItem.Password(
            id = id,
            state = state,
        )
    }
    val passwordSuggestions = createItem2(
        prefix = prefix,
        key = "password",
        args = args,
        getSuggestion = { it.login?.password },
        selectedFlow = password.state.flow.map { it.text },
        concealed = true,
        onClick = {
            password.state.flow.value.onChange?.invoke(it)
        },
    )

    val totpState = kotlin.run {
        val sink = mutablePersistedFlow("totp") {
            args.initialValue?.login?.totp?.raw.orEmpty()
        }
        val state = mutableComposeState(sink)
        val totpFlow = sink
            .debounce(80L)
            .map { raw ->
                val token = if (raw.isBlank()) {
                    null
                } else {
                    TotpToken.parse(raw).getOrNull()
                }
                token
            }
            .onStart {
                val initialState = null
                emit(initialState)
            }
        val flow = combine(
            sink
                .map { raw ->
                    TextFieldModel2(
                        state = state,
                        text = raw,
                        onChange = state::value::set,
                    )
                },
            totpFlow,
        ) { value, totp ->
            AddStateItem.Totp.State(
                value = value,
                copyText = copyText,
                totpToken = totp,
            )
        }
        LocalStateItem(
            flow = flow
                .persistingStateIn(
                    scope = screenScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = AddStateItem.Totp.State(
                        value = TextFieldModel2.empty,
                        copyText = copyText,
                        totpToken = null,
                    ),
                ),
            populator = {
                this.copy(
                    login = this.login.copy(
                        totp = it.value.text,
                    ),
                )
            },
        )
    }
    val totp = AddStateItem.Totp(
        id = "$prefix.totp",
        state = totpState,
    )
    val totpSuggestions = createItem2(
        prefix = prefix,
        key = "totp",
        args = args,
        getSuggestion = { it.login?.totp?.raw },
        selectedFlow = totp.state.flow.map { it.value.text },
        concealed = true,
        onClick = {
            totp.state.flow.value.value.onChange?.invoke(it)
        },
    )

    val items = listOfNotNull<AddStateItem>(
        username,
        usernameSuggestions,
        password,
        passwordSuggestions,
        totp,
        totpSuggestions,
    )
    return TmpLogin(
        username = username,
        password = password,
        totp = totp,
        items = items,
    )
}

private suspend fun RememberStateFlowScope.produceCardState(
    args: AddRoute.Args,
): TmpCard {
    val prefix = "card"

    suspend fun <Item> RememberStateFlowScope.createItem2(
        prefix: String,
        key: String,
        initialMonth: String? = null,
        initialYear: String? = null,
        populator: CreateRequest.(AddStateItem.DateMonthYear.State) -> CreateRequest,
        factory: (String, LocalStateItem<AddStateItem.DateMonthYear.State>) -> Item,
    ): Item {
        val id = "$prefix.$key"

        val monthSink = mutablePersistedFlow("$id.month") { initialMonth.orEmpty() }
        val monthState = mutableComposeState(monthSink)
        val monthFlow = monthSink
            .map { value ->
                val model = TextFieldModel2(
                    state = monthState,
                    text = value,
                    onChange = monthState::value::set,
                )
                model
            }

        val yearSink = mutablePersistedFlow("$id.year") { initialYear.orEmpty() }
        val yearState = mutableComposeState(yearSink)
        val yearFlow = yearSink
            .map { value ->
                val model = TextFieldModel2(
                    state = yearState,
                    text = value,
                    onChange = yearState::value::set,
                )
                model
            }

        return factory(
            id,
            LocalStateItem(
                flow = combine(
                    monthFlow,
                    yearFlow,
                ) { month, year ->
                    AddStateItem.DateMonthYear.State(
                        month = month,
                        year = year,
                        onClick = {
                            val route = registerRouteResultReceiver(
                                DatePickerRoute(
                                    DatePickerRoute.Args(
                                        month = month.text.toIntOrNull(),
                                        year = year.text.toIntOrNull(),
                                    ),
                                ),
                            ) { result ->
                                if (result is DatePickerResult.Confirm) {
                                    monthState.value = result.month.value
                                        .toString()
                                        .padStart(2, '0')
                                    yearState.value = result.year.value.toString()
                                }
                            }
                            val intent = NavigationIntent.NavigateToRoute(route)
                            navigate(intent)
                        },
                    )
                }
                    .persistingStateIn(
                        scope = screenScope,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = AddStateItem.DateMonthYear.State(
                            month = TextFieldModel2.empty,
                            year = TextFieldModel2.empty,
                            onClick = {
                                val route = registerRouteResultReceiver(
                                    DatePickerRoute(
                                        DatePickerRoute.Args(),
                                    ),
                                ) {
                                }
                                val intent = NavigationIntent.NavigateToRoute(route)
                                navigate(intent)
                            },
                        ),
                    ),
                populator = populator,
            ),
        )
    }

    suspend fun RememberStateFlowScope.createItem2(
        prefix: String,
        key: String,
        label: String,
        initialMonth: String? = null,
        initialYear: String? = null,
        populator: CreateRequest.(AddStateItem.DateMonthYear.State) -> CreateRequest,
    ) = createItem2(
        prefix = prefix,
        key = key,
        initialMonth = initialMonth,
        initialYear = initialYear,
        populator = populator,
    ) { id, state ->
        AddStateItem.DateMonthYear(
            id = id,
            state = state,
            label = label,
        )
    }

    suspend fun createItem(
        key: String,
        label: String? = null,
        hint: String? = null,
        initialValue: String? = null,
        singleLine: Boolean = false,
        autocompleteOptions: ImmutableList<String> = persistentListOf(),
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        lens: Optional<CreateRequest, String>,
    ) = createItem(
        prefix = prefix,
        key = key,
        label = label,
        hint = hint,
        initialValue = initialValue,
        singleLine = singleLine,
        autocompleteOptions = autocompleteOptions,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        populator = {
            lens.set(this, it.value.text)
        },
    )

    val ff2 = "\\D".toRegex()

    suspend fun createItem4(
        key: String,
        label: String? = null,
        initialValue: String? = null,
        autocompleteOptions: ImmutableList<String> = persistentListOf(),
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        lens: Optional<CreateRequest, String>,
    ) = kotlin.run {
        val id = "$prefix.$key"

        val sink = mutablePersistedFlow(id) { initialValue.orEmpty() }
        val state = mutableComposeState(sink)
        val state2 = LocalStateItem(
            flow = sink
                .map { value ->
                    val isValid = kotlin.run {
                        val n = value.replace(ff2, "")
                        if (n.isBlank()) {
                            return@run true
                        }
                        val luhn by lazy {
                            validLuhn(n)
                        }
                        val t = creditCards
                            .asSequence()
                            // Use eager pattern to detect card type even if
                            // the user hasn't finished typing.
                            .filter { creditCardType ->
                                val result = creditCardType.eagerPattern.find(n)
                                result?.groups?.isNotEmpty() == true
                            }
                            .filter { creditCardType ->
                                if (n.length < creditCardType.digits.first) {
                                    // This card could still be this type.
                                    true
                                } else if (n.length in creditCardType.digits) {
                                    // This card must pass strict regex.
                                    creditCardType.pattern.matches(n) &&
                                            (!creditCardType.luhn || luhn)
                                } else {
                                    false
                                }
                            }
                            .firstOrNull()
                        t != null
                    }
                    val badge = if (!isValid) {
                        TextFieldModel2.Vl(
                            type = TextFieldModel2.Vl.Type.WARNING,
                            text = translate(Res.strings.error_invalid_card_number),
                        )
                    } else {
                        null
                    }

                    val model = TextFieldModel2(
                        state = state,
                        text = value,
                        hint = "4111 1111 1111 1111",
                        vl = badge,
                        autocompleteOptions = autocompleteOptions,
                        onChange = state::value::set,
                    )
                    AddStateItem.Text.State(
                        value = model,
                        label = label,
                        singleLine = true,
                        keyboardOptions = keyboardOptions,
                        visualTransformation = visualTransformation,
                    )
                }
                .persistingStateIn(
                    scope = screenScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = AddStateItem.Text.State(
                        value = TextFieldModel2.empty,
                        label = label,
                    ),
                ),
            populator = {
                lens.set(this, it.value.text)
            },
        )
        return@run AddStateItem.Text(
            id = id,
            state = state2,
        )
    }

    val card = args.initialValue?.card
    val number = createItem4(
        key = "number",
        label = translate(Res.strings.card_number),
        initialValue = card?.number,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Number,
        ),
        visualTransformation = GenericSeparatorVisualTransformation(),
        lens = CreateRequest.card.number,
    )
    val ff = number.state.flow
        .map { state ->
            val n = state.value.text.replace(ff2, "")
            val t = creditCards
                .asSequence()
                // Use eager pattern to detect card type even if
                // the user hasn't finished typing.
                .filter { creditCardType ->
                    val result = creditCardType.eagerPattern.find(n)
                    result?.groups?.isNotEmpty() == true
                }
                .filter { creditCardType ->
                    if (n.length < creditCardType.digits.first) {
                        // This card could still be this type.
                        true
                    } else if (n.length in creditCardType.digits) {
                        // This card must pass strict regex.
                        creditCardType.pattern.matches(n)
                    } else {
                        false
                    }
                }
                .firstOrNull()
            t
        }

    val numberSuggestions = createItem2Txt(
        prefix = prefix,
        key = "number",
        args = args,
        getSuggestion = { it.card?.number },
        field = number,
    )
    val cardholderName = createItem(
        key = "cardholderName",
        label = translate(Res.strings.cardholder_name),
        initialValue = card?.cardholderName,
        singleLine = true,
        lens = CreateRequest.card.cardholderName,
    )
    val cardholderNameSuggestions = createItem2Txt(
        prefix = prefix,
        key = "cardholderName",
        args = args,
        getSuggestion = { it.card?.cardholderName },
        field = cardholderName,
    )
    val brandAutocompleteVariants = creditCards
        .asSequence()
        .map { type -> type.name }
        .toPersistentList()
    val brand = createItem(
        key = "brand",
        label = translate(Res.strings.card_type),
        initialValue = card?.brand,
        singleLine = true,
        hint = brandAutocompleteVariants
            .firstOrNull(),
        autocompleteOptions = brandAutocompleteVariants,
        lens = CreateRequest.card.brand,
    )
    val brandSuggestions = createItem2Txt(
        prefix = prefix,
        key = "brand",
        args = args,
        getSuggestion = { it.card?.brand },
        field = brand,
    )
    val fromDate = createItem2(
        prefix = prefix,
        key = "from",
        label = translate(Res.strings.valid_from),
        initialMonth = card?.fromMonth,
        initialYear = card?.fromYear,
        populator = {
            var out = this
            out = CreateRequest.card.fromMonth.set(out, it.month.text)
            out = CreateRequest.card.fromYear.set(out, it.year.text)
            out
        },
    )
//    val fromMonth = createItem(
//        key = "fromMonth",
//        label = "From month",
//        initialValue = card?.fromMonth,
//        lens = CreateRequest.card.fromMonth,
//    )
//    val fromMonthSuggestions = createItem2Txt(
//        prefix = prefix,
//        key = "fromMonth",
//        args = args,
//        getSuggestion = { it.card?.fromMonth },
//        field = fromMonth,
//    )
//    val fromYear = createItem(
//        key = "fromYear",
//        label = "From year",
//        initialValue = card?.fromYear,
//        lens = CreateRequest.card.fromYear,
//    )
//    val fromYearSuggestions = createItem2Txt(
//        prefix = prefix,
//        key = "fromYear",
//        args = args,
//        getSuggestion = { it.card?.fromYear },
//        field = fromYear,
//    )
    val expDate = createItem2(
        prefix = prefix,
        key = "exp",
        label = translate(Res.strings.expiry_date),
        initialMonth = card?.expMonth,
        initialYear = card?.expYear,
        populator = {
            var out = this
            out = CreateRequest.card.expMonth.set(out, it.month.text)
            out = CreateRequest.card.expYear.set(out, it.year.text)
            out
        },
    )
//    val expMonth = createItem(
//        key = "expMonth",
//        label = "Exp month",
//        initialValue = card?.expMonth,
//        lens = CreateRequest.card.expMonth,
//    )
//    val expMonthSuggestions = createItem2Txt(
//        prefix = prefix,
//        key = "expMonth",
//        args = args,
//        getSuggestion = { it.card?.expMonth },
//        field = expMonth,
//    )
//    val expYear = createItem(
//        key = "expYear",
//        label = "Exp year",
//        initialValue = card?.expYear,
//        lens = CreateRequest.card.expYear,
//    )
//    val expYearSuggestions = createItem2Txt(
//        prefix = prefix,
//        key = "expYear",
//        args = args,
//        getSuggestion = { it.card?.expYear },
//        field = expYear,
//    )
    val code = createItem(
        key = "code",
        label = translate(Res.strings.card_cvv),
        hint = "111",
        initialValue = card?.code,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Number,
        ),
        lens = CreateRequest.card.code,
    )
    val codeSuggestions = createItem2Txt(
        prefix = prefix,
        key = "code",
        args = args,
        getSuggestion = { it.card?.code },
        field = code,
    )
    return TmpCard(
        cardholderName = cardholderName,
        brand = brand,
        number = number,
        fromDate = fromDate,
        expDate = expDate,
        code = code,
        items = listOfNotNull<AddStateItem>(
            number,
            numberSuggestions,
            brand,
            brandSuggestions,
            code,
            codeSuggestions,
            expDate,
            fromDate,
            cardholderName,
            cardholderNameSuggestions,
        ),
    )
}

class GenericSeparatorVisualTransformation : VisualTransformation {
    private val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int =
            offset + offset.minus(1).div(4)

        override fun transformedToOriginal(offset: Int): Int =
            when (offset) {
                in 0..4 -> offset
                else -> offset - offset.div(5)
            }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = text.text
            .chunked(4)
            .joinToString(" ")
        return TransformedText(
            text = AnnotatedString(text = formatted),
            offsetMapping,
        )
    }
}

private suspend fun RememberStateFlowScope.produceIdentityState(
    args: AddRoute.Args,
): TmpIdentity {
    val prefix = "identity"

    suspend fun createItem(
        key: String,
        label: String? = null,
        initialValue: String? = null,
        singleLine: Boolean = false,
        autocompleteOptions: ImmutableList<String> = persistentListOf(),
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        lens: Optional<CreateRequest, String>,
    ) = createItem(
        prefix = prefix,
        key = key,
        label = label,
        initialValue = initialValue,
        singleLine = singleLine,
        autocompleteOptions = autocompleteOptions,
        keyboardOptions = keyboardOptions,
        populator = {
            lens.set(this, it.value.text)
        },
    )

    val identity = args.initialValue?.identity
    val title = createItem(
        key = "title",
        label = translate(Res.strings.identity_title),
        initialValue = identity?.title,
        singleLine = true,
        lens = CreateRequest.identity.title,
    )
    val titleSuggestions = createItem2Txt(
        prefix = prefix,
        key = "title",
        args = args,
        getSuggestion = { it.identity?.title },
        field = title,
    )
    val firstName = createItem(
        key = "firstName",
        label = translate(Res.strings.identity_first_name),
        initialValue = identity?.firstName ?: args.autofill?.personName,
        singleLine = true,
        lens = CreateRequest.identity.firstName,
    )
    val firstNameSuggestions = createItem2Txt(
        prefix = prefix,
        key = "firstName",
        args = args,
        getSuggestion = { it.identity?.firstName },
        field = firstName,
    )
    val middleName = createItem(
        key = "middleName",
        label = translate(Res.strings.identity_middle_name),
        initialValue = identity?.middleName,
        singleLine = true,
        lens = CreateRequest.identity.middleName,
    )
    val middleNameSuggestions = createItem2Txt(
        prefix = prefix,
        key = "middleName",
        args = args,
        getSuggestion = { it.identity?.middleName },
        field = middleName,
    )
    val lastName = createItem(
        key = "lastName",
        label = translate(Res.strings.identity_last_name),
        initialValue = identity?.lastName,
        singleLine = true,
        lens = CreateRequest.identity.lastName,
    )
    val lastNameSuggestions = createItem2Txt(
        prefix = prefix,
        key = "lastName",
        args = args,
        getSuggestion = { it.identity?.lastName },
        field = lastName,
    )
    val address1 = createItem(
        key = "address1",
        label = translate(Res.strings.address1),
        initialValue = identity?.address1,
        singleLine = true,
        lens = CreateRequest.identity.address1,
    )
    val address1Suggestions = createItem2Txt(
        prefix = prefix,
        key = "address1",
        args = args,
        getSuggestion = { it.identity?.address1 },
        field = address1,
    )
    val address2 = createItem(
        key = "address2",
        label = translate(Res.strings.address2),
        initialValue = identity?.address2,
        singleLine = true,
        lens = CreateRequest.identity.address2,
    )
    val address2Suggestions = createItem2Txt(
        prefix = prefix,
        key = "address2",
        args = args,
        getSuggestion = { it.identity?.address2 },
        field = address2,
    )
    val address3 = createItem(
        key = "address3",
        label = translate(Res.strings.address3),
        initialValue = identity?.address3,
        singleLine = true,
        lens = CreateRequest.identity.address3,
    )
    val address3Suggestions = createItem2Txt(
        prefix = prefix,
        key = "address3",
        args = args,
        getSuggestion = { it.identity?.address3 },
        field = address3,
    )
    val city = createItem(
        key = "city",
        label = translate(Res.strings.city),
        initialValue = identity?.city,
        singleLine = true,
        lens = CreateRequest.identity.city,
    )
    val citySuggestions = createItem2Txt(
        prefix = prefix,
        key = "city",
        args = args,
        getSuggestion = { it.identity?.city },
        field = city,
    )
    val state = createItem(
        key = "state",
        label = translate(Res.strings.state),
        initialValue = identity?.state,
        singleLine = true,
        lens = CreateRequest.identity.state,
    )
    val stateSuggestions = createItem2Txt(
        prefix = prefix,
        key = "state",
        args = args,
        getSuggestion = { it.identity?.state },
        field = state,
    )
    val postalCode = createItem(
        key = "postalCode",
        label = translate(Res.strings.postal_code),
        initialValue = identity?.postalCode,
        singleLine = true,
        lens = CreateRequest.identity.postalCode,
    )
    val postalCodeSuggestions = createItem2Txt(
        prefix = prefix,
        key = "postalCode",
        args = args,
        getSuggestion = { it.identity?.postalCode },
        field = postalCode,
    )
    val country = createItem(
        key = "country",
        label = translate(Res.strings.country),
        initialValue = identity?.country,
        singleLine = true,
        lens = CreateRequest.identity.country,
    )
    val countrySuggestions = createItem2Txt(
        prefix = prefix,
        key = "country",
        args = args,
        getSuggestion = { it.identity?.country },
        field = country,
    )
    val company = createItem(
        key = "company",
        label = translate(Res.strings.company),
        initialValue = identity?.company,
        singleLine = true,
        lens = CreateRequest.identity.company,
    )
    val companySuggestions = createItem2Txt(
        prefix = prefix,
        key = "country",
        args = args,
        getSuggestion = { it.identity?.company },
        field = company,
    )
    val email = createItem(
        key = "email",
        label = translate(Res.strings.email),
        initialValue = args.autofill?.email ?: identity?.email,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Email,
        ),
        lens = CreateRequest.identity.email,
    )
    val emailSuggestions = createItem2Txt(
        prefix = prefix,
        key = "email",
        args = args,
        getSuggestion = { it.identity?.email },
        field = email,
    )
    val phone = createItem(
        key = "phone",
        label = translate(Res.strings.phone_number),
        initialValue = args.autofill?.phone ?: identity?.phone,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Phone,
        ),
        lens = CreateRequest.identity.phone,
    )
    val phoneSuggestions = createItem2Txt(
        prefix = prefix,
        key = "phone",
        args = args,
        getSuggestion = { it.identity?.phone },
        field = phone,
    )
    val ssn = createItem(
        key = "ssn",
        label = translate(Res.strings.ssn),
        initialValue = identity?.ssn,
        singleLine = true,
        lens = CreateRequest.identity.ssn,
    )
    val ssnSuggestions = createItem2Txt(
        prefix = prefix,
        key = "ssn",
        args = args,
        getSuggestion = { it.identity?.ssn },
        field = ssn,
    )
    val username = createItem(
        key = "username",
        label = translate(Res.strings.username),
        initialValue = args.autofill?.username ?: identity?.username,
        singleLine = true,
        lens = CreateRequest.identity.username,
    )
    val usernameSuggestions = createItem2Txt(
        prefix = prefix,
        key = "username",
        args = args,
        getSuggestion = { it.identity?.username },
        field = username,
    )
    val passportNumber = createItem(
        key = "passportNumber",
        label = translate(Res.strings.passport_number),
        initialValue = identity?.passportNumber,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
        ),
        lens = CreateRequest.identity.passportNumber,
    )
    val passportNumberSuggestions = createItem2Txt(
        prefix = prefix,
        key = "passportNumber",
        args = args,
        getSuggestion = { it.identity?.passportNumber },
        field = passportNumber,
    )
    val licenseNumber = createItem(
        key = "licenseNumber",
        label = translate(Res.strings.license_number),
        initialValue = identity?.licenseNumber,
        singleLine = true,
        lens = CreateRequest.identity.licenseNumber,
    )
    val licenseNumberSuggestions = createItem2Txt(
        prefix = prefix,
        key = "licenseNumber",
        args = args,
        getSuggestion = { it.identity?.licenseNumber },
        field = licenseNumber,
    )
    return TmpIdentity(
        title = title,
        firstName = firstName,
        middleName = middleName,
        lastName = lastName,
        address1 = address1,
        address2 = address2,
        address3 = address3,
        city = city,
        state = state,
        postalCode = postalCode,
        country = country,
        company = company,
        email = email,
        phone = phone,
        ssn = ssn,
        username = username,
        passportNumber = passportNumber,
        licenseNumber = licenseNumber,
        items = listOfNotNull<AddStateItem>(
            title,
            titleSuggestions,
            firstName,
            firstNameSuggestions,
            middleName,
            middleNameSuggestions,
            lastName,
            lastNameSuggestions,
            AddStateItem.Section(
                id = "identity.section.address",
            ),
            address1,
            address1Suggestions,
            address2,
            address2Suggestions,
            address3,
            address3Suggestions,
            city,
            citySuggestions,
            state,
            stateSuggestions,
            postalCode,
            postalCodeSuggestions,
            country,
            countrySuggestions,
            AddStateItem.Section(
                id = "identity.section.misc",
            ),
            email,
            emailSuggestions,
            phone,
            phoneSuggestions,
            ssn,
            ssnSuggestions,
            username,
            usernameSuggestions,
            company,
            companySuggestions,
            passportNumber,
            passportNumberSuggestions,
            licenseNumber,
            licenseNumberSuggestions,
        ),
    )
}

private suspend fun RememberStateFlowScope.produceNoteState(
    args: AddRoute.Args,
    markdown: Boolean,
): TmpNote {
    val prefix = "notes"

    val note = kotlin.run {
        val id = "$prefix.note"

        val initialValue = args.initialValue?.notes

        val sink = mutablePersistedFlow(id) { initialValue.orEmpty() }
        val state = mutableComposeState(sink)
        val stateItem = LocalStateItem(
            flow = sink
                .map { value ->
                    val model = TextFieldModel2(
                        state = state,
                        text = value,
                        hint = "Add any notes about this item here",
                        onChange = state::value::set,
                    )
                    model
                }
                .persistingStateIn(
                    scope = screenScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = TextFieldModel2.empty,
                ),
            populator = { state ->
                CreateRequest.note.set(this, state.text)
            },
        )
        AddStateItem.Note(
            id = id,
            state = stateItem,
            markdown = markdown,
        )
    }
    val noteSuggestions = createItem2(
        prefix = prefix,
        key = "note",
        args = args,
        getSuggestion = { it.notes },
        selectedFlow = note.state.flow.map { it.text },
        onClick = {
            note.state.flow.value.onChange?.invoke(it)
        },
    )
    return TmpNote(
        note = note,
        items = listOfNotNull<AddStateItem>(
            note,
            noteSuggestions,
        ),
    )
}

private suspend fun RememberStateFlowScope.createItem(
    prefix: String,
    key: String,
    label: String? = null,
    hint: String? = null,
    initialValue: String? = null,
    autocompleteOptions: ImmutableList<String> = persistentListOf(),
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    populator: CreateRequest.(AddStateItem.Text.State) -> CreateRequest,
) = createItem(
    prefix = prefix,
    key = key,
    label = label,
    hint = hint,
    initialValue = initialValue,
    autocompleteOptions = autocompleteOptions,
    singleLine = singleLine,
    keyboardOptions = keyboardOptions,
    visualTransformation = visualTransformation,
    populator = populator,
) { id, state ->
    AddStateItem.Text(
        id = id,
        state = state,
    )
}

private suspend fun <Item> RememberStateFlowScope.createItem(
    prefix: String,
    key: String,
    label: String? = null,
    hint: String? = null,
    initialValue: String? = null,
    autocompleteOptions: ImmutableList<String> = persistentListOf(),
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    populator: CreateRequest.(AddStateItem.Text.State) -> CreateRequest,
    factory: (String, LocalStateItem<AddStateItem.Text.State>) -> Item,
): Item {
    val id = "$prefix.$key"

    val sink = mutablePersistedFlow(id) { initialValue.orEmpty() }
    val state = mutableComposeState(sink)
    return factory(
        id,
        LocalStateItem(
            flow = sink
                .map { value ->
                    val model = TextFieldModel2(
                        state = state,
                        text = value,
                        hint = hint,
                        autocompleteOptions = autocompleteOptions,
                        onChange = state::value::set,
                    )
                    AddStateItem.Text.State(
                        value = model,
                        label = label,
                        singleLine = singleLine,
                        keyboardOptions = keyboardOptions,
                        visualTransformation = visualTransformation,
                    )
                }
                .persistingStateIn(
                    scope = screenScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = AddStateItem.Text.State(
                        value = TextFieldModel2.empty,
                        label = label,
                    ),
                ),
            populator = populator,
        ),
    )
}

private suspend fun RememberStateFlowScope.createItem2Txt(
    prefix: String,
    key: String,
    args: AddRoute.Args,
    getSuggestion: (DSecret) -> String?,
    field: AddStateItem.Text,
    concealed: Boolean = false,
) = createItem2(
    prefix = prefix,
    key = key,
    args = args,
    getSuggestion = getSuggestion,
    selectedFlow = field.state.flow.map { it.value.text },
    concealed = concealed,
    onClick = {
        field.state.flow.value.value.onChange?.invoke(it)
    },
)

private suspend fun RememberStateFlowScope.createItem2(
    prefix: String,
    key: String,
    args: AddRoute.Args,
    getSuggestion: (DSecret) -> String?,
    selectedFlow: Flow<String>,
    concealed: Boolean = false,
    onClick: (String) -> Unit,
): AddStateItem.Suggestion? {
    val finalKey = "$prefix.$key.suggestions"
    val suggestions = args.merge
        ?.ciphers
        ?.mapNotNull { cipher ->
            val suggestion = getSuggestion(cipher)
                // skip blank suggestions
                ?.takeIf { it.isNotBlank() }
                ?: return@mapNotNull null
            suggestion to cipher
        }
        ?.groupBy { it.first }
        ?.takeIf { it.size > 1 }
        ?: return null

    val items = suggestions
        .map { (suggestion, secrets) ->
            val suggestionKey = "$finalKey.$suggestion"
            val text = if (concealed) {
                obscurePassword(suggestion)
            } else {
                suggestion
            }
            AddStateItem.Suggestion.Item(
                key = suggestionKey,
                value = suggestion,
                text = text,
                source = secrets
                    .joinToString { it.second.name },
                onClick = onClick
                    .partially1(suggestion),
                selected = false,
            )
        }
        .toImmutableList()
    val flow = selectedFlow
        .map { selectedValue ->
            val i = items
                .map { item ->
                    val selected = selectedValue == item.value
                    item.copy(selected = selected)
                }
                .toImmutableList()
            AddStateItem.Suggestion.State(
                items = i,
            )
        }
        .persistingStateIn(
            scope = screenScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AddStateItem.Suggestion.State(persistentListOf()),
        )
    return AddStateItem.Suggestion(
        id = finalKey,
        state = LocalStateItem(
            flow = flow,
        ),
    )
}