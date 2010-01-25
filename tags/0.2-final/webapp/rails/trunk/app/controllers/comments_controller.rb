#
#  Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
class CommentsController < ApplicationController
  
  before_filter :authorize, :except => [:index, :show]
  before_filter :validate_event
  layout "site"
  
  after_filter :expire_comment, :only => [:create, :destroy, :update]

  # GET /events/1/comments
  def index
    @comments = Comment.find_all_by_event_id(params[:event_id])
    respond_to do |format|
      format.html # index.html.erb
    end
  end
  
  # GET /events/1/comments/1
  def show
    find_comment do
      respond_to do |format|
        format.html # show.html.erb
      end
    end
  end
  
  # GET /events/1/comments/new
  def new
    @comment = Comment.new(:rating => 0)
    respond_to do |format|
      format.html # new.html.erb
    end
  end
  
  # POST /events/1/comments
  def create
    @comment = Comment.new(params[:comment])
    @comment.event_id = params[:event_id]
    @comment.user_id = session[:user_id]
    if @comment.save
      respond_to do |format|
        flash[:notice] = "Thanks for your comment."
        format.html { redirect_to(event_path(params[:event_id])) }
        format.js { render :layout => false }
      end
    else
      respond_to do |format|
        flash[:error] = "Sorry, your comment could not be created."
        format.html { render :action => "new" }
        format.js do
          render :update do |page|
            page.refresh_messages
          end
        end
      end
    end
  end
  
  # GET /events/1/comments/1/edit
  def edit
    find_comment do
      if logged_in_as(@comment.author)
        respond_to do |format|
          format.html # edit.html.erb
        end
      else
        respond_to do |format|
          flash[:error] = "You did not write this comment."
          format.html { redirect_to(event_path(params[:event_id])) }
        end
      end
    end
  end
  
  # PUT /events/1/comments/1
  def update
    find_comment do
      if logged_in_as(@comment.author)
        if @comment.update_attributes(params[:comment])
          respond_to do |format|
            flash[:notice] = "Comment updated."
            format.html { redirect_to(event_path(params[:event_id])) }
            format.js { render :layout => false }
          end
        else
          respond_to do |format|
            flash[:error] = "Unable to update comment."
            format.html { render :action => "edit" }
            format.js do 
              render :update do |page|
                page.refresh_messages
              end
            end
          end
        end
      else
        respond_to do |format|
          flash[:error] = "You did not write this comment."
          format.html { redirect_to(event_path(params[:event_id])) }
          format.js do 
            render :update do |page|
              page.refresh_messages
            end
          end
        end
      end
    end
  end
  
  # GET /events/1/comments/1/delete
  def delete
    find_comment do
      respond_to do |format|
        format.html # delete.html.erb
      end
    end
  end
  
  # DELETE /events/1/comments/1
  def destroy
    find_comment do
      if logged_in_as(@comment.author)
        @comment.destroy
        respond_to do |format|
          flash[:notice] = "Comment deleted."
          format.html { redirect_to event_path(params[:event_id]) }
          format.js { render :layout => false }
        end
      else
        respond_to do |format|
          flash[:error] = "You can't delete someone else's comment."
          format.html { redirect_to event_path(params[:event_id]) }
          format.js do
            render :update do |page|
              page.refresh_messages
            end
          end
        end
      end
    end
  end 
  
  private
  
  def find_comment
    begin
      @comment = Comment.find(params[:id])
      yield
    rescue ActiveRecord::RecordNotFound
      respond_to do |format|
        flash[:error] = "Comment does not exist."
        format.html { redirect_to event_path(params[:event_id]) }
      end
    end
  end
  
  def expire_comment
    expire_fragment(:controller => "events", :action => "show", :id => @comment.event_id, :part => "event_comments")
  end

end
