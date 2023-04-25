@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName",
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import androidx.compose.material.*
import androidx.compose.runtime.*
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.WeatherStationDonnaTheme
import net.ddns.rkdawenterprises.weatherstationdonna.Main_activity
import net.ddns.rkdawenterprises.weatherstationdonna.User_settings

@Composable
fun Main(main_activity: Main_activity, m_weather_data: Weather_data_view_model)
{
    val is_night_mode = User_settings.is_night_mode_derived(main_activity).collectAsState(initial = )
//    val is_night_mode : MutableState<Boolean> = remember { mutableStateOf(true); }

    WeatherStationDonnaTheme(mutableStateOf(true)) {
        Text("Hello World")
    }
}

//                    if(!is_ok)
//                    {
//                        val message = m_context.resources.getString(R.string.not_allowed_to_get_weather_data_unless_over_wifi);
//                        Log.d(Main_activity.LOG_TAG, "is_ok_to_fetch_data: $message");
//                        Snackbar.make(m_binding.root,
//                                      message,
//                                      Snackbar.LENGTH_SHORT).setAction(R.string.ok) {}.show();
//                    }

//        val scaffold_state = rememberScaffoldState();
//        Scaffold(
//            topBar = { Top_app_bar(scaffold_state, main_activity) }
//        ) { innerPadding->
//            LazyColumn(contentPadding = innerPadding) {
//                item {
//                    Header("Weather Station Donna @ Hot Springs, AR - Hot Springs, AR, USA");
//                }
//                item {
//                    FeaturedPost(
//                        post = featured,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//                item {
//                    Header(stringResource(R.string.popular))
//                }
//                items(posts) { post ->
//                    PostItem(post = post)
//                    Divider(startIndent = 72.dp)
//                }
//            }
//        }
//    }

//@Composable
//private fun Top_app_bar(scaffold_state: ScaffoldState, main_activity: Main_activity)
//{
//    val coroutine_scope = rememberCoroutineScope();
//    var manual_refresh by remember { mutableStateOf(1); }
//    var show_menu by remember { mutableStateOf(false); }
//    TopAppBar(
//        navigationIcon = {
//            IconButton(onClick = {
//                coroutine_scope.launch {
//                    val message = String.format("%s %s",
//                                                main_activity.resources.getString(R.string.exiting),
//                                                main_activity.resources.getString(R.string.app_name));
//                    scaffold_state.snackbarHostState.showSnackbar(
//                        message = message,
//                        actionLabel = main_activity.resources.getString(R.string.ok),
//                        duration = SnackbarDuration.Long
//                    )
//                }
//
//                main_activity.exit_app();
//            },
//                       modifier = Modifier.padding(horizontal = 12.dp)) {
//                Icon(imageVector = Icons.Filled.ArrowBack,
//                     contentDescription = stringResource(R.string.navigate_back))
//            }
//        },
//        title = {
//            Text(text = stringResource(R.string.app_name))
//        },
//        backgroundColor = MaterialTheme.colors.primarySurface,
//        actions = {
//            IconButton(onClick = { manual_refresh++ }) {
//                Icon(imageVector = Icons.Filled.Refresh,
//                     contentDescription = stringResource(id = R.string.action_refresh_weather_data))
//            }
//            IconButton(onClick = { show_menu = !show_menu }) {
//                Icon(imageVector = Icons.Filled.MoreVert,
//                     contentDescription = null)
//            }
//            DropdownMenu(
//                expanded = show_menu,
//                onDismissRequest = { show_menu = false }
//            ) {
//                val night_mode_dialog_open = remember { mutableStateOf(false); }
//                DropdownMenuItem(onClick = {
//                    val selections = main_activity.resources.getStringArray(R.array.night_mode_selection);
//                    night_mode_dialog_open.value = true;
//                    if (night_mode_dialog_open.value)
//                    {
//                        AlertDialog( onDismissRequest = {
//                            night_mode_dialog_open.value = false
//                        },)
//                    }
//                    AlertDialogBuilder(main_activity)
//                            .setTitle(R.string.select_night_mode)
//                            .setSingleChoiceItems(selections, main_activity.get_night_mode_selection(), null)
//                            .setPositiveButton(R.string.ok) { dialog, _->
//                                val selection = (dialog as AlertDialog).listView.checkedItemPosition;
//                                main_activity.put_night_mode_selection(selection);
//                                main_activity.update_night_mode(selection)
//                            }
//                            .setNegativeButton(R.string.cancel) { dialog, _-> dialog.cancel(); }
//                            .show();
//                }) {
//                    Icon(imageVector = Icons.Filled.DarkMode,
//                         contentDescription = stringResource(id = R.string.action_set_night_mode_theme));
//                    Spacer(Modifier.size(ButtonDefaults.IconSpacing));
//                    Text(stringResource(id = R.string.action_set_night_mode_theme));
//                }
//            }
//        }
//    )
//}

//@Composable
//fun Header(
//    text: String,
//    modifier: Modifier = Modifier
//)
//{
//    Surface(
//        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
//        contentColor = MaterialTheme.colors.primary,
//        modifier = modifier.semantics { heading() }
//    ) {
//        Text(
//            text = text,
//            style = MaterialTheme.typography.subtitle2,
//            modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp)
//        )
//    }
//}

//@Composable
//fun FeaturedPost(
//    post: Post,
//    modifier: Modifier = Modifier
//) {
//    Card(modifier) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { /* onClick */ }
//        ) {
//            Image(
//                painter = painterResource(post.imageId),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .heightIn(min = 180.dp)
//                    .fillMaxWidth()
//            )
//            Spacer(Modifier.height(16.dp))
//
//            val padding = Modifier.padding(horizontal = 16.dp)
//            Text(
//                text = post.title,
//                style = MaterialTheme.typography.h6,
//                modifier = padding
//            )
//            Text(
//                text = post.metadata.author.name,
//                style = MaterialTheme.typography.body2,
//                modifier = padding
//            )
//            PostMetadata(post, padding)
//            Spacer(Modifier.height(16.dp))
//        }
//    }
//}
//
//@Composable
//private fun PostMetadata(
//    post: Post,
//    modifier: Modifier = Modifier
//) {
//    val divider = "  •  "
//    val tagDivider = "  "
//    val text = buildAnnotatedString {
//        append(post.metadata.date)
//        append(divider)
//        append(stringResource(R.string.read_time, post.metadata.readTimeMinutes))
//        append(divider)
//        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
//            background = MaterialTheme.colors.primary.copy(alpha = 0.1f)
//        )
//        post.tags.forEachIndexed { index, tag ->
//            if (index != 0) {
//                append(tagDivider)
//            }
//            withStyle(tagStyle) {
//                append(" ${tag.uppercase(Locale.getDefault())} ")
//            }
//        }
//    }
//    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
//        Text(
//            text = text,
//            style = MaterialTheme.typography.body2,
//            modifier = modifier
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun PostItem(
//    post: Post,
//    modifier: Modifier = Modifier
//) {
//    ListItem(
//        modifier = modifier
//            .clickable { /* todo */ }
//            .padding(vertical = 8.dp),
//        icon = {
//            Image(
//                painter = painterResource(post.imageThumbId),
//                contentDescription = null,
//                modifier = Modifier.clip(shape = MaterialTheme.shapes.small)
//            )
//        },
//        text = {
//            Text(text = post.title)
//        },
//        secondaryText = {
//            PostMetadata(post)
//        }
//    )
//}
//
//@Preview("Post Item")
//@Composable
//private fun PostItemPreview() {
//    val post = remember { PostRepo.getFeaturedPost() }
//    JetnewsTheme {
//        Surface {
//            PostItem(post = post)
//        }
//    }
//}
//
//@Preview("Featured Post")
//@Composable
//private fun FeaturedPostPreview() {
//    val post = remember { PostRepo.getFeaturedPost() }
//    JetnewsTheme {
//        FeaturedPost(post = post)
//    }
//}
//
//@Preview("Featured Post • Dark")
//@Composable
//private fun FeaturedPostDarkPreview() {
//    val post = remember { PostRepo.getFeaturedPost() }
//    JetnewsTheme(darkTheme = true) {
//        FeaturedPost(post = post)
//    }
//}
//
//@Preview("Home")
//@Composable
//private fun HomePreview() {
//    Home()
//}
