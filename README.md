# Project 3 - *Parsetagram*

**Parsetagram** is a photo sharing app similar to Instagram but using Parse as its backend.

Time spent: **16** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User can view the last 20 posts submitted to "Instagram".
- [x] The user should switch between different tabs - viewing all posts (feed view), compose (capture photos form camera) and profile tabs (posts made) using fragments and a Bottom Navigation View. (2 points)
- [x] User can pull to refresh the last 20 posts submitted to "Instagram".

The following **optional** features are implemented:

- [ ] User sees app icon in home screen and styled bottom navigation view
- [x] Style the feed to look like the real Instagram feed.
- [x] User can load more posts once he or she reaches the bottom of the feed using infinite scrolling.
- [ ] Show the username and creation time for each post.
- [ ] User can tap a post to view post details, including timestamp and caption.
- [x] User Profiles
      - [ ] Allow the logged in user to add a profile photo
      - [ ] Display the profile photo with each post
      - [x] Tapping on a post's username or profile photo goes to that user's profile page and shows a grid view of the user's posts
- [ ] User can comment on a post and see all comments for each post in the post details screen.
- [x] User can like a post and see number of likes for each post in the post details screen.

The following **additional** features are implemented:

- [x] Post deletion on profile page
- [x] Profile following
- [x] Custom camera view

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/ogtega/parsetagram/blob/main/parsetagram.gif?raw=true' title='Video Walkthrough' width='300' alt='Video Walkthrough' />

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Coil](https://coil-kt.github.io/coil/) - Image loading library for Android
- [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) - Android data paging library
- [Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started) - Navigation Architecture Component

## License

    Copyright 2022 Teslim Olunlade

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.